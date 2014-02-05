package crazypants.enderio.conduit.liquid;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidHandler;
import crazypants.enderio.conduit.ConduitUtil;
import crazypants.enderio.conduit.IConduit;
import crazypants.util.BlockCoord;

public class LiquidConduitNetwork extends AbstractTankConduitNetwork<LiquidConduit> {

  public LiquidConduitNetwork() {
    super(LiquidConduit.class);
  }

  private long timeAtLastApply;

  private int maxFlowsPerTick = 10;
  private int lastFlowIndex = 0;

  private boolean printFlowTiming = false;

  private int pushToken = 0;

  private int inputVolume;

  private int outputVolume;

  private boolean inputLocked = false;

  public boolean lockNetworkForFill() {
    if(inputLocked) {
      return false;
    }
    inputLocked = true;
    return true;
  }

  public void unlockNetworkFromFill() {
    inputLocked = false;
  }

  @Override
  public void onUpdateEntity(IConduit conduit) {
    World world = conduit.getBundle().getEntity().worldObj;
    if(world == null) {
      return;
    }
    if(world.isRemote || liquidType == null) {
      return;
    }

    long curTime = world.getTotalWorldTime();
    if(curTime > 0 && curTime != timeAtLastApply) {
      timeAtLastApply = curTime;
      // 1000 water, 6000 lava
      if(liquidType != null && liquidType.getFluid() != null) {
        int visc = Math.max(1000, liquidType.getFluid().getViscosity());
        if(curTime % (visc / 500) == 0) {
          long start = System.nanoTime();
          if(doFlow() && printFlowTiming) {
            long took = System.nanoTime() - start;
            double secs = took / 1000000000.0;
            System.out.println("LiquidConduitNetwork.onUpdateEntity: took " + secs + " secs, " + (secs * 1000) + " millis");
          }
        }
      }
    }
    if(!fluidTypeLocked && isEmpty()) {
      setFluidType(null);
    }

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

    int pushToken = getNextPushToken();
    List<FlowAction> actions = new ArrayList<FlowAction>();
    for (int i = 0; i < Math.min(maxFlowsPerTick, conduits.size()); i++) {

      if(lastFlowIndex >= conduits.size()) {
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
    List<LiquidConduit> toEmpty = new ArrayList<LiquidConduit>();
    for (LiquidConduit con : conduits) {
      if(con != null && con.getTank().getFluidAmount() < 10) {
        toEmpty.add(con);
      } else {
        //some of the conduits have fluid left in them so don't do the final drain yet
        return result;
      }

    }
    if(toEmpty.isEmpty()) {
      return result;
    }

    List<LocatedFluidHandler> externals = new ArrayList<LocatedFluidHandler>();
    for (AbstractTankConduit con : conduits) {
      Set<ForgeDirection> extCons = con.getExternalConnections();

      for (ForgeDirection dir : extCons) {
        if(con.canOutputToDir(dir)) {
          IFluidHandler externalTank = con.getExternalHandler(dir);
          if(externalTank != null) {
            externals.add(new LocatedFluidHandler(externalTank, con.getLocation().getLocation(dir), dir.getOpposite()));
          }
        }
      }
    }
    if(externals.isEmpty()) {
      return result;
    }

    for (LiquidConduit con : toEmpty) {
      drainConduitToNearestExternal(con, externals);
    }

    return result;
  }

  @Override
  public void setFluidTypeLocked(boolean fluidTypeLocked) {
    super.setFluidTypeLocked(fluidTypeLocked);
    if(!fluidTypeLocked && isEmpty()) {
      setFluidType(null);
    }
  }

  private boolean isEmpty() {
    for (LiquidConduit con : conduits) {
      if(con.tank.getFluidAmount() > 0) {
        return false;
      }
    }
    return true;
  }

  private void drainConduitToNearestExternal(LiquidConduit con, List<LocatedFluidHandler> externals) {
    BlockCoord conLoc = con.getLocation();
    FluidStack toDrain = con.getTank().getFluid();
    if(toDrain == null) {
      return;
    }
    int closestDistance = Integer.MAX_VALUE;
    LocatedFluidHandler closestTank = null;
    for (LocatedFluidHandler lh : externals) {
      int distance = lh.bc.distanceSquared(conLoc);
      if(distance < closestDistance) {
        int couldFill = lh.tank.fill(lh.dir, toDrain.copy(), false);
        if(couldFill > 0) {
          closestTank = lh;
          closestDistance = distance;
        }
      }
    }

    if(closestTank != null) {
      int filled = closestTank.tank.fill(closestTank.dir, toDrain.copy(), true);
      con.getTank().addAmount(-filled);
    }

  }

  private void flowFrom(LiquidConduit con, List<FlowAction> actions, int pushPoken) {

    ConduitTank tank = con.getTank();
    int totalAmount = tank.getFluidAmount();
    if(totalAmount <= 0) {
      return;
    }

    int maxFlowVolume = 20;

    // First flow all we can down, then balance the rest
    if(con.getConduitConnections().contains(ForgeDirection.DOWN)) {
      BlockCoord loc = con.getLocation().getLocation(ForgeDirection.DOWN);
      ILiquidConduit dc = ConduitUtil.getConduit(con.getBundle().getEntity().worldObj, loc.x, loc.y, loc.z, ILiquidConduit.class);
      if(dc instanceof LiquidConduit) {
        LiquidConduit downCon = (LiquidConduit) dc;
        int filled = ((LiquidConduit) downCon).fill(ForgeDirection.UP, tank.getFluid().copy(), false, false, pushPoken);
        int actual = filled;
        actual = Math.min(actual, tank.getFluidAmount());
        actual = Math.min(actual, downCon.getTank().getAvailableSpace());
        tank.addAmount(-actual);
        downCon.getTank().addAmount(actual);
      }
    }

    totalAmount = tank.getFluidAmount();
    if(totalAmount <= 0) {
      return;
    }
    FluidStack available = tank.getFluid();
    int totalRequested = 0;
    int numRequests = 0;
    // Then to external connections
    for (ForgeDirection dir : con.getExternalConnections()) {
      if(con.canOutputToDir(dir)) {
        IFluidHandler extCon = con.getExternalHandler(dir);
        if(extCon != null) {
          int amount = extCon.fill(dir.getOpposite(), available.copy(), false);
          if(amount > 0) {
            totalRequested += amount;
            numRequests++;
          }
        }
      }
    }

    if(numRequests > 0) {
      int amountPerRequest = Math.min(totalAmount, totalRequested) / numRequests;
      amountPerRequest = Math.min(maxFlowVolume, amountPerRequest);

      FluidStack requestSource = available.copy();
      requestSource.amount = amountPerRequest;
      for (ForgeDirection dir : con.getExternalConnections()) {
        if(con.canOutputToDir(dir)) {
          IFluidHandler extCon = con.getExternalHandler(dir);
          if(extCon != null) {
            int amount = extCon.fill(dir.getOpposite(), requestSource.copy(), true);
            if(amount > 0) {
              outputedToExternal(amount);
              tank.addAmount(-amount);
            }
          }
        }
      }
    }

    totalAmount = tank.getFluidAmount();
    if(totalAmount <= 0) {
      return;
    }
    int totalCapacity = tank.getCapacity();

    BlockCoord loc = con.getLocation();
    Collection<ILiquidConduit> connections =
        ConduitUtil.getConnectedConduits(con.getBundle().getEntity().worldObj,
            loc.x, loc.y, loc.z, ILiquidConduit.class);
    int numTargets = 0;
    for (ILiquidConduit n : connections) {
      LiquidConduit neighbour = (LiquidConduit) n;
      if(canFlowTo(con, neighbour)) { // can only flow within same network
        totalAmount += neighbour.getTank().getFluidAmount();
        totalCapacity += neighbour.getTank().getCapacity();
        numTargets++;
      }
    }

    float targetRatio = (float) totalAmount / totalCapacity;
    int flowVolume = (int) Math.floor((targetRatio - tank.getFilledRatio()) * tank.getCapacity());
    flowVolume = Math.min(maxFlowVolume, flowVolume);

    if(Math.abs(flowVolume) < 2) {
      return; // dont bother with transfers of less than a thousands of a bucket
    }

    for (ILiquidConduit n : connections) {
      LiquidConduit neigbour = (LiquidConduit) n;
      if(canFlowTo(con, neigbour)) { // can only flow within same network
        flowVolume = (int) Math.floor((targetRatio - neigbour.getTank().getFilledRatio()) * neigbour.getTank().getCapacity());
        if(flowVolume != 0) {
          actions.add(new FlowAction(con, neigbour, flowVolume));
        }
      }
    }

  }

  private boolean canFlowTo(LiquidConduit con, LiquidConduit neighbour) {
    if(con == null || neighbour == null) {
      return false;
    }
    if(neighbour.getNetwork() != this) {
      return false;
    }
    if(neighbour.getLocation().y > con.getLocation().y) {
      return false;
    }
    float nr = neighbour.getTank().getFilledRatio();
    if(nr >= con.getTank().getFilledRatio()) {
      return false;
    }
    return true;
  }

  static class FlowAction {
    final LiquidConduit from;
    final LiquidConduit to;
    final int amount;

    FlowAction(LiquidConduit fromIn, LiquidConduit toIn, int amountIn) {
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
      if(amount != 0) {

        // don't take more than it has
        int actual = Math.min(amount, from.getTank().getFluidAmount());
        // and don't add more than it can take
        actual = Math.min(actual, to.getTank().getAvailableSpace());

        if(from != null && to != null) {
          from.getTank().addAmount(-actual);
          to.getTank().addAmount(actual);
        }

      }
    }

  }

  static class LocatedFluidHandler {
    final IFluidHandler tank;
    final BlockCoord bc;
    final ForgeDirection dir;

    LocatedFluidHandler(IFluidHandler tank, BlockCoord bc, ForgeDirection dir) {
      this.tank = tank;
      this.bc = bc;
      this.dir = dir;
    }

  }

}
