package crazypants.enderio.conduit.liquid;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.liquids.ITankContainer;
import net.minecraftforge.liquids.LiquidStack;
import crazypants.enderio.conduit.AbstractConduitNetwork;
import crazypants.enderio.conduit.ConduitUtil;
import crazypants.enderio.conduit.IConduit;
import crazypants.util.BlockCoord;

public class LiquidConduitNetwork extends AbstractConduitNetwork<ILiquidConduit> {

  private LiquidStack liquidType;
  private long timeAtLastApply;

  private int maxFlowsPerTick = 10;
  private int lastFlowIndex = 0;

  private boolean printFlowTiming = false;

  private int pushToken = 0;
  
  private int inputVolume;
  
  private int outputVolume;
  
  @Override
  public Class<? extends ILiquidConduit> getBaseConduitType() {
    return ILiquidConduit.class;
  }

  public LiquidStack getFluidType() {
    return liquidType;
  }

  @Override
  public void addConduit(ILiquidConduit con) {
    super.addConduit(con);
    con.setFluidType(liquidType);
  }

  public void setFluidType(LiquidStack newType) {
    if (liquidType != null && liquidType.isLiquidEqual(newType)) {
      return;
    }
    if (newType != null) {
      liquidType = newType.copy();
      liquidType.amount = 0;
    } else {
      liquidType = null;
    }
    for (ILiquidConduit conduit : conduits) {
      conduit.setFluidType(liquidType);
    }
  }

  public boolean canAcceptLiquid(LiquidStack acceptable) {
    return areFluidsCompatable(liquidType, acceptable);
  }

  public static boolean areFluidsCompatable(LiquidStack a, LiquidStack b) {
    if (a == null || b == null) {
      return true;
    }
    return a.isLiquidEqual(b);
  }

  public int getTotalVolume() {
    int totalVolume = 0;
    for (ILiquidConduit con : conduits) {
      totalVolume += con.getTank().getFluidAmount();
    }
    return totalVolume;
  }

  @Override
  public void onUpdateEntity(IConduit conduit) {
    World world = conduit.getBundle().getEntity().worldObj;
    if (world == null) {
      return;
    }
    if (world.isRemote || liquidType == null) {
      return;
    }

    long curTime = world.getTotalWorldTime();
    if (curTime != timeAtLastApply) {
      timeAtLastApply = curTime;
      // 1000 water, 6000 lava
      if (liquidType != null) {
        // int visc = liquidType.getFluid().getViscosity();
        int visc = 1000;
        if (curTime % (visc / 500) == 0) {
          long start = System.nanoTime();
          if (doFlow() && printFlowTiming) {
            long took = System.nanoTime() - start;
            double secs = took / 1000000000.0;
            System.out.println("LiquidConduitNetwork.onUpdateEntity: took " + secs + " secs, " + (secs * 1000) + " millis");
          }
        }
      }
    }
    
//    boolean printed = false;
//    for(ILiquidConduit con : conduits) {
//      if(con.getTank().getFilledRatio() > 1) {
//        System.out.println("LiquidConduitNetwork.onUpdateEntity: capacity= " + con.getTank().getCapacity() + " amount= " + con.getTank().getFluidAmount());
//        printed = true;
//      }
//    }
//    
//    if(printed) {
//      System.out.println("LiquidConduitNetwork.onUpdateEntity: ");
//    }
    
  }

  void addedFromExternal(int res) {
    inputVolume += res;    
  }

  void outputedToExternal(int filled) {
    outputVolume += filled;
  }

  int getNextPushToken() {
    return ++pushToken;
  }

  private boolean doFlow() {

    // int preVol = 0;
    // for (ILiquidConduit con : conduits) {
    // preVol += con.getTank().getAmount();
    // }

    int pushToken = getNextPushToken();
    List<FlowAction> actions = new ArrayList<FlowAction>();
    for (int i = 0; i < Math.min(maxFlowsPerTick, conduits.size()); i++) {

      if (lastFlowIndex >= conduits.size()) {
        lastFlowIndex = 0;
      }
      flowFrom(conduits.get(lastFlowIndex), actions, pushToken);
      ++lastFlowIndex;

    }
    for (FlowAction action : actions) {
      action.apply();
    }

    boolean result = !actions.isEmpty();

    // Flush any tanks with a tiny bit left
    List<ILiquidConduit> toEmpty = new ArrayList<ILiquidConduit>();
    for (ILiquidConduit con : conduits) {
      if (con.getTank().getFluidAmount() < 10) {
        toEmpty.add(con);
      }

    }
    if (toEmpty.isEmpty()) {
      return result;
    }

    List<LocatedFluidHandler> externals = new ArrayList<LocatedFluidHandler>();
    for (ILiquidConduit con : conduits) {
      Set<ForgeDirection> extCons = con.getExternalConnections();

      for (ForgeDirection dir : extCons) {
        if (con.canOutputToDir(dir)) {
          ITankContainer externalTank = con.getExternalHandler(dir);
          if (externalTank != null) {
            externals.add(new LocatedFluidHandler(externalTank, con.getLocation().getLocation(dir), dir.getOpposite()));
          }
        }
      }
    }
    if (externals.isEmpty()) {
      return result;
    }

    for (ILiquidConduit con : toEmpty) {
      drainConduitToNearestExternal(con, externals);
    }

    // int postVol = 0;
    // for (ILiquidConduit con : conduits) {
    // postVol += con.getTank().getAmount();
    // }
    // if (preVol != postVol) {
    // System.out.println("LiquidConduitNetwork.doFlow: net change of: " +
    // (postVol - preVol));
    // }

    return result;
  }

  private void drainConduitToNearestExternal(ILiquidConduit con, List<LocatedFluidHandler> externals) {
    BlockCoord conLoc = con.getLocation();
    LiquidStack toDrain = con.getTank().getLiquid();
    if (toDrain == null) {
      return;
    }
    int closestDistance = Integer.MAX_VALUE;
    LocatedFluidHandler closestTank = null;
    for (LocatedFluidHandler lh : externals) {
      int distance = lh.bc.distanceSquared(conLoc);
      if (distance < closestDistance) {
        int couldFill = lh.tank.fill(lh.dir, toDrain.copy(), false);
        if (couldFill > 0) {
          closestTank = lh;
          closestDistance = distance;
        }
      }
    }

    if (closestTank != null) {
      int filled = closestTank.tank.fill(closestTank.dir, toDrain.copy(), true);
      con.getTank().addAmount(-filled);
    }

  }

  private void flowFrom(ILiquidConduit con, List<FlowAction> actions, int pushPoken) {

    ConduitTank tank = con.getTank();
    int totalAmount = tank.getFluidAmount();
    if (totalAmount <= 0) {
      return;
    }

    int maxFlowVolume = 20;

    // First flow all we can down, then balance the rest
    if (con.getConduitConnections().contains(ForgeDirection.DOWN)) {
      BlockCoord loc = con.getLocation().getLocation(ForgeDirection.DOWN);
      ILiquidConduit downCon = ConduitUtil.getConduit(con.getBundle().getEntity().worldObj, loc.x, loc.y, loc.z, ILiquidConduit.class);
      int filled = downCon.fill(ForgeDirection.UP, tank.getLiquid().copy(), false, false, pushPoken);
      int actual = filled;
      actual = Math.min(actual, tank.getFluidAmount());
      actual = Math.min(actual, downCon.getTank().getAvailableSpace());
      tank.addAmount(-actual);
      downCon.getTank().addAmount(actual);
    }

    totalAmount = tank.getFluidAmount();
    if (totalAmount <= 0) {
      return;
    }
    LiquidStack available = tank.getLiquid();
    int totalRequested = 0;
    int numRequests = 0;
    // Then to external connections
    for (ForgeDirection dir : con.getExternalConnections()) {
      if (con.canOutputToDir(dir)) {
        ITankContainer extCon = con.getExternalHandler(dir);
        if (extCon != null) {
          int amount = extCon.fill(dir.getOpposite(), available.copy(), false);
          if (amount > 0) {
            totalRequested += amount;
            numRequests++;
          }
        }
      }
    }

    if (numRequests > 0) {
      int amountPerRequest = Math.min(totalAmount, totalRequested) / numRequests;
      amountPerRequest = Math.min(maxFlowVolume, amountPerRequest);

      LiquidStack requestSource = available.copy();
      requestSource.amount = amountPerRequest;
      for (ForgeDirection dir : con.getExternalConnections()) {
        if (con.canOutputToDir(dir)) {
          ITankContainer extCon = con.getExternalHandler(dir);
          if (extCon != null) {
            int amount = extCon.fill(dir.getOpposite(), requestSource.copy(), true);
            if (amount > 0) {
              outputedToExternal(amount);
              tank.addAmount(-amount);
            }
          }
        }
      }
    }

    // TODO: Liquid can currently flow upwards from a down connection

    totalAmount = tank.getFluidAmount();
    if (totalAmount <= 0) {
      return;
    }
    int totalCapacity = tank.getCapacity();

    BlockCoord loc = con.getLocation();
    Collection<ILiquidConduit> connections =
        ConduitUtil.getConnectedConduits(con.getBundle().getEntity().worldObj,
            loc.x, loc.y, loc.z, ILiquidConduit.class);
    int numTargets = 0;
    for (ILiquidConduit neighbour : connections) {
      if (canFlowTo(con, neighbour)) { // can only flow within same network
        totalAmount += neighbour.getTank().getFluidAmount();
        totalCapacity += neighbour.getTank().getCapacity();
        numTargets++;
      }
    }

    float targetRatio = (float) totalAmount / totalCapacity;
    //int netChange = 0;
    int flowVolume = (int)Math.floor((targetRatio - tank.getFilledRatio()) * tank.getCapacity());
    flowVolume = Math.min(maxFlowVolume, flowVolume);

    if (Math.abs(flowVolume) < 2) {
      return; // dont bother with transfers of less than a thousands of a bucket
    }

    for (ILiquidConduit neigbour : connections) {
      if (canFlowTo(con, neigbour)) { // can only flow within same network
        flowVolume = (int)Math.floor((targetRatio - neigbour.getTank().getFilledRatio()) * neigbour.getTank().getCapacity());
        if (flowVolume != 0) {
          actions.add(new FlowAction(con, neigbour, flowVolume));
          //netChange += flowVolume;
        }
      }
    }

//    if (netChange != 0) {
//      actions.add(new FlowAction(con, -netChange));
//    }
    }

  private boolean canFlowTo(ILiquidConduit con, ILiquidConduit neighbour) {
    if (neighbour.getNetwork() != this) {
      return false;
    }
    if (neighbour.getLocation().y > con.getLocation().y) {
      return false;
    }
    float nr = neighbour.getTank().getFilledRatio();
    if (nr >= con.getTank().getFilledRatio()) {
      return false;
    }
    return true;
  }

  static class FlowAction {
    final ILiquidConduit from;
    final ILiquidConduit to;
    final int amount;

    FlowAction(ILiquidConduit fromIn, ILiquidConduit toIn, int amountIn) {
      if(amountIn < 0) {        
        to = fromIn;
        from = toIn;
        amount = -amountIn;
      } else {
        to = toIn;
        from = fromIn;
        amount = amountIn;
    }
    }

    void apply() {
      if (amount != 0) {

        //don't take more than it has
        int actual = Math.min(amount, from.getTank().getFluidAmount());
        //and don't add more than it can take
        actual = Math.min(actual, to.getTank().getAvailableSpace());
        
        from.getTank().addAmount(-actual);
        to.getTank().addAmount(actual);
        
      }
    }

  }

  static class LocatedFluidHandler {
    final ITankContainer tank;
    final BlockCoord bc;
    final ForgeDirection dir;

    LocatedFluidHandler(ITankContainer tank, BlockCoord bc, ForgeDirection dir) {
      this.tank = tank;
      this.bc = bc;
      this.dir = dir;
}

  }

}
