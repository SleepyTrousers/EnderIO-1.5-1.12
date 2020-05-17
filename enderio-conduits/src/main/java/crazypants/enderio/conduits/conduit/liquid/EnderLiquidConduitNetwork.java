package crazypants.enderio.conduits.conduit.liquid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import com.enderio.core.common.fluid.IFluidWrapper.ITankInfoWrapper;
import com.enderio.core.common.util.RoundRobinIterator;

import crazypants.enderio.base.Log;
import crazypants.enderio.base.filter.fluid.IFluidFilter;
import crazypants.enderio.conduits.conduit.AbstractConduitNetwork;
import crazypants.enderio.conduits.config.ConduitConfig;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

public class EnderLiquidConduitNetwork extends AbstractConduitNetwork<ILiquidConduit, EnderLiquidConduit> {

  List<NetworkTank> tanks = new ArrayList<NetworkTank>();
  Map<NetworkTankKey, NetworkTank> tankMap = new HashMap<NetworkTankKey, NetworkTank>();

  Map<NetworkTank, RoundRobinIterator<NetworkTank>> iterators;

  boolean filling;

  public EnderLiquidConduitNetwork() {
    super(EnderLiquidConduit.class, ILiquidConduit.class);
  }

  public void connectionChanged(@Nonnull EnderLiquidConduit con, @Nonnull EnumFacing conDir) {
    NetworkTankKey key = new NetworkTankKey(con, conDir);
    NetworkTank tank = new NetworkTank(con, conDir);

    tanks.remove(tank); // remove old tank, NB: =/hash is only calced on location and dir
    tanks.add(tank);
    tankMap.remove(key);
    tankMap.put(key, tank);

    tanks.sort((left, right) -> right.priority - left.priority);
  }

  public boolean extractFrom(@Nonnull EnderLiquidConduit con, @Nonnull EnumFacing conDir) {
    final NetworkTank tank = getTank(con, conDir);
    if (!tank.isValid()) {
      return false;
    }

    final int fullLimit = (int) (ConduitConfig.fluid_tier3_extractRate.get() * getExtractSpeedMultiplier(tank));
    if (tank.supportsMultipleTanks) {
      int limit = fullLimit;
      for (ITankInfoWrapper tankInfoWrapper : tank.externalTank.getTankInfoWrappers()) {
        final IFluidTankProperties tankProperties = tankInfoWrapper.getIFluidTankProperties();
        if (tankProperties.canDrain()) {
          limit -= tryTransfer(con, conDir, tank, tankProperties.getContents(), limit);
          if (limit <= 0) {
            return true;
          }
        }
      }
      return limit < fullLimit;
    } else {
      return tryTransfer(con, conDir, tank, tank.externalTank.getAvailableFluid(), fullLimit) > 0;
    }
  }

  private int tryTransfer(@Nonnull EnderLiquidConduit con, @Nonnull EnumFacing conDir, @Nonnull NetworkTank from, FluidStack toDrain, int limit) {
    if (toDrain == null || toDrain.amount <= 0 || !matchedFilter(toDrain, con, conDir, true)) {
      return 0;
    }

    // (1) Our limit
    FluidStack draining = toDrain.copy();
    draining.amount = Math.min(draining.amount, limit);

    // (2) Simulate putting all of that into targets
    int amountAccepted = fillFrom(from, draining.copy(), false);
    if (amountAccepted <= 0) {
      return 0;
    }
    draining.amount = Math.min(draining.amount, amountAccepted);

    // (3) Drain what we could insert from the source
    FluidStack drained = from.externalTank.drain(draining);
    if (drained == null || drained.amount <= 0) {
      return 0;
    }

    // (4) Insert what we just drained into targets for real---and hope it actually works out...
    int amountFilled = fillFrom(from, drained.copy(), true);
    if (amountFilled > drained.amount) {
      Log.warn(
          "EnderLiquidConduit at " + con.getBundle().getLocation() + " in dimension " + con.getBundle().getBundleworld().provider.getDimensionType().getId()
              + ": Inserted fluid volume (" + amountFilled + "mB) is more than we tried to insert (" + drained.amount + "mB).");
    } else if (amountFilled < drained.amount) {
      Log.warn(
          "EnderLiquidConduit at " + con.getBundle().getLocation() + " in dimension " + con.getBundle().getBundleworld().provider.getDimensionType().getId()
              + ": Inserted fluid volume (" + amountFilled + "mB) is less than when we asked the target how much to insert (" + drained.amount
              + "mB). This means that one of the blocks connected to this conduit line has a bug.");
      FluidStack toPutBack = toDrain.copy();
      toPutBack.amount = drained.amount - amountFilled;
      int putBack = from.externalTank.fill(toPutBack.copy());
      if (putBack < toPutBack.amount) {
        Log.warn("EnderLiquidConduit at " + con.getBundle().getLocation() + " in dimension "
            + con.getBundle().getBundleworld().provider.getDimensionType().getId() + ": In addition, putting back " + toPutBack.amount
            + "mB into the source tank failed, leading to " + (toPutBack.amount - putBack) + "mB being voided.");
      }
    }
    return amountFilled;
  }

  @Nonnull
  private NetworkTank getTank(@Nonnull EnderLiquidConduit con, @Nonnull EnumFacing conDir) {
    return tankMap.get(new NetworkTankKey(con, conDir));
  }

  // this is called when a neighbor block pushes fluid into the conduit
  public int fillFrom(@Nonnull EnderLiquidConduit con, @Nonnull EnumFacing conDir, FluidStack resource, boolean doFill) {
    return fillFrom(getTank(con, conDir), resource, doFill);
  }

  public int fillFrom(@Nonnull NetworkTank tank, FluidStack resource, boolean doFill) {

    if (filling || resource == null) {
      return 0;
    }

    /**
     * Some targets change dependent on other targets (e.g. 2 conduit connections to the same tank). Simulation will differ from execution in this case. To
     * prevent simulation from reporting higher amounts that execution can really move, we stop simulation after the first target. This limits throughput, but
     * prevents errors (including voiding fluids and backspill into blocks with separate input and output tanks). It also makes sure that higher priority
     * targets must be full before lower priority tanks get filled.
     */
    boolean firstOnly = !doFill;

    RoundRobinIterator<NetworkTank> iteratorForTank = getIteratorForTank(tank);
    if (!doFill) {
      iteratorForTank = iteratorForTank.copy();
    }
    try {

      filling = true;

      if (resource == null || !matchedFilter(resource, tank.con, tank.conDir, true)) {
        return 0;
      }

      resource = resource.copy();
      resource.amount = Math.min(resource.amount, (int) (ConduitConfig.fluid_tier3_maxIO.get() * getExtractSpeedMultiplier(tank)));
      int filled = 0;
      int remaining = resource.amount;

      for (NetworkTank target : iteratorForTank) {
        if ((!target.equals(tank) || tank.selfFeed) && target.acceptsOuput && target.isValid() && target.inputColor == tank.outputColor
            && matchedFilter(resource, target.con, target.conDir, false)) {
          int vol = doFill ? target.externalTank.fill(resource.copy()) : target.externalTank.offer(resource.copy());
          remaining -= vol;
          filled += vol;
          if (remaining <= 0 || (firstOnly && filled > 0)) {
            return filled;
          }
          resource.amount = remaining;
        }
      }
      return filled;

    } finally {
      if (!tank.roundRobin) {
        iteratorForTank.reset();
      }
      filling = false;
    }
  }

  private float getExtractSpeedMultiplier(NetworkTank tank) {
    return tank.con.getExtractSpeedMultiplier(tank.conDir);
  }

  private boolean matchedFilter(FluidStack drained, @Nonnull EnderLiquidConduit con, @Nonnull EnumFacing conDir, boolean isInput) {
    if (drained == null) {
      return false;
    }
    IFluidFilter filter = con.getFilter(conDir, isInput);
    if (filter == null || filter.isEmpty()) {
      return true;
    }
    return filter.matchesFilter(drained);
  }

  private RoundRobinIterator<NetworkTank> getIteratorForTank(@Nonnull NetworkTank tank) {
    if (iterators == null) {
      iterators = new HashMap<NetworkTank, RoundRobinIterator<NetworkTank>>();
    }
    RoundRobinIterator<NetworkTank> res = iterators.get(tank);
    if (res == null) {
      res = new RoundRobinIterator<NetworkTank>(tanks);
      iterators.put(tank, res);
    }
    return res;
  }

  public IFluidTankProperties[] getTankProperties(@Nonnull EnderLiquidConduit con, @Nonnull EnumFacing conDir) {
    List<IFluidTankProperties> res = new ArrayList<IFluidTankProperties>(tanks.size());
    NetworkTank tank = getTank(con, conDir);
    for (NetworkTank target : tanks) {
      if (!target.equals(tank) && target.isValid()) {
        for (ITankInfoWrapper info : target.externalTank.getTankInfoWrappers()) {
          res.add(info.getIFluidTankProperties());
        }
      }
    }
    return res.toArray(new IFluidTankProperties[res.size()]);
  }

  static class NetworkTankKey {

    EnumFacing conDir;
    BlockPos conduitLoc;

    public NetworkTankKey(@Nonnull EnderLiquidConduit con, @Nonnull EnumFacing conDir) {
      this(con.getBundle().getLocation(), conDir);
    }

    public NetworkTankKey(@Nonnull BlockPos conduitLoc, @Nonnull EnumFacing conDir) {
      this.conDir = conDir;
      this.conduitLoc = conduitLoc;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((conDir == null) ? 0 : conDir.hashCode());
      result = prime * result + ((conduitLoc == null) ? 0 : conduitLoc.hashCode());
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      }
      if (obj == null) {
        return false;
      }
      if (getClass() != obj.getClass()) {
        return false;
      }
      NetworkTankKey other = (NetworkTankKey) obj;
      if (conDir != other.conDir) {
        return false;
      }
      if (conduitLoc == null) {
        if (other.conduitLoc != null) {
          return false;
        }
      } else if (!conduitLoc.equals(other.conduitLoc)) {
        return false;
      }
      return true;
    }

  }

}
