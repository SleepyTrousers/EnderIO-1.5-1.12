package crazypants.enderio.conduit.liquid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import crazypants.enderio.Config;
import crazypants.enderio.Log;
import crazypants.enderio.conduit.AbstractConduitNetwork;
import crazypants.enderio.conduit.ConnectionMode;
import crazypants.util.BlockCoord;
import crazypants.util.FluidUtil;
import crazypants.util.RoundRobinIterator;

public class EnderLiquidConduitNetwork extends AbstractConduitNetwork<ILiquidConduit, EnderLiquidConduit> {

  public static final int MAX_EXTRACT_PER_TICK = Config.enderFluidConduitExtractRate;
  public static final int MAX_IO_PER_TICK = Config.enderFluidConduitMaxIoRate;

  List<NetworkTank> tanks = new ArrayList<NetworkTank>();
  
  Map<NetworkTank, RoundRobinIterator<NetworkTank>> iterators;

  public EnderLiquidConduitNetwork() {
    super(EnderLiquidConduit.class);
  }

  @Override
  public Class<ILiquidConduit> getBaseConduitType() {
    return ILiquidConduit.class;
  }

  public void connectionChanged(EnderLiquidConduit con, ForgeDirection conDir) {    
    NetworkTank tank = new NetworkTank(con, conDir);
    tanks.remove(tank); // remove old tank, NB: =/hash is only calced on location and dir
    tanks.add(tank);
  }

  public boolean extractFrom(EnderLiquidConduit con, ForgeDirection conDir) {
    NetworkTank tank = new NetworkTank(con, conDir);
    if(!tank.isValid()) {
      return false;
    }
    FluidStack drained = tank.externalTank.drain(conDir.getOpposite(), MAX_EXTRACT_PER_TICK, false);
    if(drained == null || drained.amount <= 0) {
      return false;
    }
    int amountAccepted = fillFrom(tank, drained, false);
    if(amountAccepted <= 0) {
      return false;
    }
    drained = tank.externalTank.drain(conDir.getOpposite(), amountAccepted, true);
    if(drained == null || drained.amount <= 0) {
      return false;
    }
    int filled = fillFrom(tank, drained, true);
    if(drained.amount != filled) {
      Log.warn("EnderLiquidConduit.extractFrom: Extracted fluid volume is not equal to inserted volume. Drained=" + drained.amount + " filled=" + filled);
    }
    return true;
  }

  public int fillFrom(EnderLiquidConduit con, ForgeDirection conDir, FluidStack resource, boolean doFill) {
    return fillFrom(new NetworkTank(con, conDir), resource, doFill);
  }

  public int fillFrom(NetworkTank tank, FluidStack resource, boolean doFill) {
    if(resource == null) {
      return 0;
    }
    resource = resource.copy();
    resource.amount = Math.min(resource.amount, MAX_IO_PER_TICK);
    int filled = 0;
    int remaining = resource.amount;
    for (NetworkTank target : getIteratorForTank(tank)) {
      if(!target.equals(tank) && target.acceptsOuput && target.isValid()) {
        int vol = target.externalTank.fill(target.tankDir, resource.copy(), doFill);
        remaining -= vol;
        filled += vol;
        if(remaining <= 0) {
          return filled;
        }
        resource.amount = remaining;
      }
    }
    return filled;
  }

  private Iterable<NetworkTank> getIteratorForTank(NetworkTank tank) {
    if(iterators == null) {
      iterators = new HashMap<NetworkTank, RoundRobinIterator<NetworkTank>>();
    }
    RoundRobinIterator<NetworkTank> res = iterators.get(tank);
    if(res == null) {
      res = new RoundRobinIterator<NetworkTank>(tanks);
      iterators.put(tank, res);
    }
    return res;
  }

  public FluidTankInfo[] getTankInfo(EnderLiquidConduit con, ForgeDirection conDir) {
    List<FluidTankInfo> res = new ArrayList<FluidTankInfo>(tanks.size());
    NetworkTank tank = new NetworkTank(con, conDir);
    for (NetworkTank target : tanks) {
      if(!target.equals(tank) && target.isValid()) {
        FluidTankInfo[] tTanks = target.externalTank.getTankInfo(target.tankDir);
        for(FluidTankInfo info: tTanks) {
          res.add(info);
        }
      }
    }
    return res.toArray(new FluidTankInfo[res.size()]);
  }

  static class NetworkTank {

    EnderLiquidConduit con;
    ForgeDirection conDir;
    IFluidHandler externalTank;
    ForgeDirection tankDir;
    BlockCoord conduitLoc;
    boolean acceptsOuput;

    public NetworkTank(EnderLiquidConduit con, ForgeDirection conDir) {
      this.con = con;
      this.conDir = conDir;
      conduitLoc = con.getLocation();
      tankDir = conDir.getOpposite();
      externalTank = FluidUtil.getExternalFluidHandler(con.getBundle().getWorld(), conduitLoc.getLocation(conDir));
      acceptsOuput = con.getConectionMode(conDir).acceptsOutput();
    }

    public boolean isValid() {
      return externalTank != null && con.getConectionMode(conDir) != ConnectionMode.DISABLED;
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
      if(this == obj)
        return true;
      if(obj == null)
        return false;
      if(getClass() != obj.getClass())
        return false;
      NetworkTank other = (NetworkTank) obj;
      if(conDir != other.conDir)
        return false;
      if(conduitLoc == null) {
        if(other.conduitLoc != null)
          return false;
      } else if(!conduitLoc.equals(other.conduitLoc))
        return false;
      return true;
    }

  }

}
