package crazypants.enderio.conduits.conduit.liquid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import com.enderio.core.common.fluid.IFluidWrapper;
import com.enderio.core.common.fluid.IFluidWrapper.ITankInfoWrapper;
import com.enderio.core.common.util.RoundRobinIterator;

import crazypants.enderio.base.conduit.ConnectionMode;
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
    tankMap.remove(key);
    tanks.add(tank);
    tankMap.put(key, tank);
  }

  public boolean extractFrom(@Nonnull EnderLiquidConduit con, @Nonnull EnumFacing conDir) {
    NetworkTank tank = getTank(con, conDir);
    if (!tank.isValid()) {
      return false;
    }
    FluidStack drained = tank.externalTank.getAvailableFluid();
    if (drained == null || drained.amount <= 0 || !matchedFilter(drained, con, conDir, true)) {
      return false;
    }
    drained = drained.copy();
    drained.amount = Math.min(drained.amount, ConduitConfig.fluid_tier3_extractRate.get());
    int amountAccepted = fillFrom(tank, drained.copy(), true);
    if (amountAccepted <= 0) {
      return false;
    }
    drained.amount = amountAccepted;
    drained = tank.externalTank.drain(drained);
    if (drained == null || drained.amount <= 0) {
      return false;
    }
    // if(drained.amount != amountAccepted) {
    // Log.warn("EnderLiquidConduit.extractFrom: Extracted fluid volume is not equal to inserted volume. Drained=" + drained.amount + " filled="
    // + amountAccepted + " Fluid: " + drained + " Accepted=" + amountAccepted);
    // }
    return true;
  }

  @Nonnull
  private NetworkTank getTank(@Nonnull EnderLiquidConduit con, @Nonnull EnumFacing conDir) {
    return tankMap.get(new NetworkTankKey(con, conDir));
  }

  public int fillFrom(@Nonnull EnderLiquidConduit con, @Nonnull EnumFacing conDir, FluidStack resource, boolean doFill) {
    return fillFrom(getTank(con, conDir), resource, doFill);
  }

  public int fillFrom(@Nonnull NetworkTank tank, FluidStack resource, boolean doFill) {

    if (filling) {
      return 0;
    }

    try {

      filling = true;

      if (resource == null || !matchedFilter(resource, tank.con, tank.conDir, true)) {
        return 0;
      }
      resource = resource.copy();
      resource.amount = Math.min(resource.amount, ConduitConfig.fluid_tier3_maxIO.get());
      int filled = 0;
      int remaining = resource.amount;
      // TODO: Only change starting pos of iterator is doFill is true so a false then true returns the same

      for (NetworkTank target : getIteratorForTank(tank)) {
        if (!target.equals(tank) && target.acceptsOuput && target.isValid() && matchedFilter(resource, target.con, target.conDir, false)) {
          int vol = doFill ? target.externalTank.fill(resource.copy()) : target.externalTank.offer(resource.copy());
          remaining -= vol;
          filled += vol;
          if (remaining <= 0) {
            return filled;
          }
          resource.amount = remaining;
        }
      }
      return filled;

    } finally {
      filling = false;
    }
  }

  private boolean matchedFilter(FluidStack drained, @Nonnull EnderLiquidConduit con, @Nonnull EnumFacing conDir, boolean isInput) {
    if (drained == null) {
      return false;
    }
    FluidFilter filter = con.getFilter(conDir, isInput);
    if (filter == null || filter.isEmpty()) {
      return true;
    }
    return filter.matchesFilter(drained);
  }

  private Iterable<NetworkTank> getIteratorForTank(@Nonnull NetworkTank tank) {
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

  static class NetworkTank {

    final @Nonnull EnderLiquidConduit con;
    // TODO Confirm dir necessity
    final @Nonnull EnumFacing conDir;
    final IFluidWrapper externalTank;
    final @Nonnull EnumFacing tankDir;
    final @Nonnull BlockPos conduitLoc;
    final boolean acceptsOuput;

    public NetworkTank(@Nonnull EnderLiquidConduit con, @Nonnull EnumFacing conDir) {
      this.con = con;
      this.conDir = conDir;
      conduitLoc = con.getBundle().getLocation();
      tankDir = conDir.getOpposite();
      externalTank = AbstractLiquidConduit.getExternalFluidHandler(con.getBundle().getBundleworld(), conduitLoc.offset(conDir), tankDir);
      acceptsOuput = con.getConnectionMode(conDir).acceptsOutput();
    }

    public boolean isValid() {
      return externalTank != null && con.getConnectionMode(conDir) != ConnectionMode.DISABLED;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + conDir.hashCode();
      result = prime * result + conduitLoc.hashCode();
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
      NetworkTank other = (NetworkTank) obj;
      if (conDir != other.conDir) {
        return false;
      }
      if (!conduitLoc.equals(other.conduitLoc)) {
        return false;
      }
      return true;
    }

  }

}
