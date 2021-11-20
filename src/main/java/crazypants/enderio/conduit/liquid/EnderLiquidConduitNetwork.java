package crazypants.enderio.conduit.liquid;

import java.util.*;

import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

import com.enderio.core.common.util.BlockCoord;

import crazypants.enderio.conduit.AbstractConduitNetwork;
import crazypants.enderio.conduit.ConnectionMode;

public class EnderLiquidConduitNetwork extends AbstractConduitNetwork<ILiquidConduit, AbstractEnderLiquidConduit> {
  private class TankIterator implements Iterator<NetworkTank> {
    private int index = -1;
    private int currentCount = 0;

    public TankIterator start() {
      currentCount = 0;
      return this;
    }

    @Override
    public boolean hasNext() {
      return !tanks.isEmpty() && currentCount < tanks.size();
    }

    @Override
    public NetworkTank next() {
      if (tanks.isEmpty()) {
        return null;
      }
      currentCount++;
      index++;
      if (index >= tanks.size()) {
        index = 0;
      }
      return tanks.get(index);
    }

    @Override
    public void remove() {
    }

    public void rewind() {
      if (index == 0)
        index = tanks.size() - 1;
      else
        index--;
      currentCount--;
    }
  }

  private final AbstractEnderLiquidConduit.Type type;

  List<NetworkTank> tanks = new ArrayList<NetworkTank>();
  Map<NetworkTankKey, NetworkTank> tankMap = new HashMap<NetworkTankKey, NetworkTank>();

  Map<NetworkTank, TankIterator> iterators;

  boolean filling;

  public EnderLiquidConduitNetwork(AbstractEnderLiquidConduit.Type type) {
    super(AbstractEnderLiquidConduit.class, ILiquidConduit.class);
    this.type = type;
  }

  public void connectionChanged(AbstractEnderLiquidConduit con, ForgeDirection conDir) {
    NetworkTankKey key = new NetworkTankKey(con, conDir);
    NetworkTank tank = new NetworkTank(con, conDir);
    tanks.remove(tank); // remove old tank, NB: =/hash is only calced on location and dir
    tankMap.remove(key);
    tanks.add(tank);
    tankMap.put(key, tank);
  }

  public boolean extractFrom(AbstractEnderLiquidConduit con, ForgeDirection conDir) {
    NetworkTank tank = getTank(con, conDir);
    if(tank == null || !tank.isValid()) {
      return false;
    }
    FluidStack drained = tank.externalTank.drain(conDir.getOpposite(), type.getMaxExtractPerTick(), false);
    if(drained == null || drained.amount <= 0 || !matchedFilter(drained, con, conDir, true)) {
      return false;
    }
    int amountAccepted = fillFrom(tank, drained.copy(), true);
    if(amountAccepted <= 0) {
      return false;
    }
    drained = tank.externalTank.drain(conDir.getOpposite(), amountAccepted, true);
    if(drained == null || drained.amount <= 0) {
      return false;
    }
    //    if(drained.amount != amountAccepted) {
    //      Log.warn("AbstractEnderLiquidConduit.extractFrom: Extracted fluid volume is not equal to inserted volume. Drained=" + drained.amount + " filled="
    //          + amountAccepted + " Fluid: " + drained + " Accepted=" + amountAccepted);
    //    }
    return true;
  }

  private NetworkTank getTank(AbstractEnderLiquidConduit con, ForgeDirection conDir) {
    return tankMap.get(new NetworkTankKey(con, conDir));
  }

  public int fillFrom(AbstractEnderLiquidConduit con, ForgeDirection conDir, FluidStack resource, boolean doFill) {
    return fillFrom(getTank(con, conDir), resource, doFill);
  }

  public int fillFrom(NetworkTank tank, FluidStack resource, boolean doFill) {

    if(filling) {
      return 0;
    }

    try {

      filling = true;

      if(resource == null || tank == null || !matchedFilter(resource, tank.con, tank.conDir, true)) {
        return 0;
      }
      resource = resource.copy();
      resource.amount = Math.min(resource.amount, type.getMaxIoPerTick());
      int filled = 0;
      int remaining = resource.amount;
      //TODO: Only change starting pos of iterator is doFill is true so a false then true returns the same

      TankIterator iterator;
      for (iterator = getIteratorForTank(tank).start(); iterator.hasNext(); ) {
        NetworkTank target = iterator.next();
        if (!target.equals(tank) && target.con.getOutputColor(target.conDir).equals(tank.con.getInputColor(tank.conDir)) && target.acceptsOuput && target.isValid() && matchedFilter(resource, target.con, target.conDir, false)) {
          int vol = target.externalTank.fill(target.tankDir, resource.copy(), doFill);
          remaining -= vol;
          filled += vol;
          if (remaining <= 0) {
            break;
          }
          resource.amount = remaining;
        }
      }
      if (!tank.con.isRoundRobin(tank.conDir))
        iterator.rewind();
      return filled;

    } finally {
      filling = false;
    }
  }

  private boolean matchedFilter(FluidStack drained, AbstractEnderLiquidConduit con, ForgeDirection conDir, boolean isInput) {
    if(drained == null || con == null || conDir == null) {
      return false;
    }
    FluidFilter filter = con.getFilter(conDir, isInput);
    if(filter == null || filter.isEmpty()) {
      return true;
    }
    return filter.matchesFilter(drained);
  }

  private TankIterator getIteratorForTank(NetworkTank tank) {
    if(iterators == null) {
      iterators = new HashMap<>();
    }
    TankIterator res = iterators.get(tank);
    if(res == null) {
      res = new TankIterator();
      iterators.put(tank, res);
    }
    return res;
  }

  public FluidTankInfo[] getTankInfo(AbstractEnderLiquidConduit con, ForgeDirection conDir) {
    List<FluidTankInfo> res = new ArrayList<FluidTankInfo>(tanks.size());
    NetworkTank tank = getTank(con, conDir);
    for (NetworkTank target : tanks) {
      if(!target.equals(tank) && target.isValid()) {
        FluidTankInfo[] tTanks = target.externalTank.getTankInfo(target.tankDir);
        if(tTanks != null) {
          for (FluidTankInfo info : tTanks) {
            res.add(info);
          }
        }
      }
    }
    return res.toArray(new FluidTankInfo[res.size()]);
  }

  static class NetworkTankKey {

    ForgeDirection conDir;
    BlockCoord conduitLoc;

    public NetworkTankKey(AbstractEnderLiquidConduit con, ForgeDirection conDir) {
      this(con.getLocation(), conDir);
    }

    public NetworkTankKey(BlockCoord conduitLoc, ForgeDirection conDir) {
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
      if(this == obj) {
        return true;
      }
      if(obj == null) {
        return false;
      }
      if(getClass() != obj.getClass()) {
        return false;
      }
      NetworkTankKey other = (NetworkTankKey) obj;
      if(conDir != other.conDir) {
        return false;
      }
      if(conduitLoc == null) {
        if(other.conduitLoc != null) {
          return false;
        }
      } else if(!conduitLoc.equals(other.conduitLoc)) {
        return false;
      }
      return true;
    }

  }

  static class NetworkTank {

    AbstractEnderLiquidConduit con;
    ForgeDirection conDir;
    IFluidHandler externalTank;
    ForgeDirection tankDir;
    BlockCoord conduitLoc;
    boolean acceptsOuput;

    public NetworkTank(AbstractEnderLiquidConduit con, ForgeDirection conDir) {
      this.con = con;
      this.conDir = conDir;
      conduitLoc = con.getLocation();
      tankDir = conDir.getOpposite();
      externalTank = AbstractLiquidConduit.getExternalFluidHandler(con.getBundle().getWorld(), conduitLoc.getLocation(conDir));
      acceptsOuput = con.getConnectionMode(conDir).acceptsOutput();
    }

    public boolean isValid() {
      return externalTank != null && con.getConnectionMode(conDir) != ConnectionMode.DISABLED;
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
      if(this == obj) {
        return true;
      }
      if(obj == null) {
        return false;
      }
      if(getClass() != obj.getClass()) {
        return false;
      }
      NetworkTank other = (NetworkTank) obj;
      if(conDir != other.conDir) {
        return false;
      }
      if(conduitLoc == null) {
        if(other.conduitLoc != null) {
          return false;
        }
      } else if(!conduitLoc.equals(other.conduitLoc)) {
        return false;
      }
      return true;
    }

  }

}
