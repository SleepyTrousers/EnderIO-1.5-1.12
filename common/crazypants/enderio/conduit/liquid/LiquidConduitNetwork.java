package crazypants.enderio.conduit.liquid;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidHandler;
import crazypants.enderio.conduit.AbstractConduitNetwork;
import crazypants.enderio.conduit.ConduitUtil;
import crazypants.enderio.conduit.IConduit;
import crazypants.util.BlockCoord;

public class LiquidConduitNetwork extends AbstractConduitNetwork<ILiquidConduit> {

  private FluidStack liquidType;
  private long timeAtLastApply;

  private int maxFlowsPerTick = 10;
  private int lastFlowIndex = 0;

  private boolean printFlowTiming = false;

  @Override
  public Class<? extends ILiquidConduit> getBaseConduitType() {
    return ILiquidConduit.class;
  }

  public FluidStack getFluidType() {
    return liquidType;
  }

  @Override
  public void addConduit(ILiquidConduit con) {
    super.addConduit(con);
    con.setFluidType(liquidType);
  }

  public void setFluidType(FluidStack newType) {
    if (liquidType != null && liquidType.isFluidEqual(newType)) {
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

  public boolean canAcceptLiquid(FluidStack acceptable) {
    return areFluidsCompatable(liquidType, acceptable);
  }

  public static boolean areFluidsCompatable(FluidStack a, FluidStack b) {
    if (a == null || b == null) {
      return true;
    }
    return a.isFluidEqual(b);
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
    if (curTime > 0 && curTime != timeAtLastApply) {
      timeAtLastApply = curTime;
      // 1000 water, 6000 lava
      if (liquidType != null && liquidType.getFluid() != null) {
        int visc = Math.max(1000, liquidType.getFluid().getViscosity());
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
  }

  private boolean doFlow() {

    // int preVol = 0;
    // for (ILiquidConduit con : conduits) {
    // preVol += con.getTank().getAmount();
    // }

    List<FlowAction> actions = new ArrayList<FlowAction>();
    for (int i = 0; i < Math.min(maxFlowsPerTick, conduits.size()); i++) {

      if (lastFlowIndex >= conduits.size()) {
        lastFlowIndex = 0;
      }
      flowFrom(conduits.get(lastFlowIndex), actions);
      ++lastFlowIndex;

    }
    for (FlowAction action : actions) {
      action.apply();
    }

    // int postVol = 0;
    // for (ILiquidConduit con : conduits) {
    // postVol += con.getTank().getAmount();
    // }
    // if (preVol != postVol) {
    // System.out.println("LiquidConduitNetwork.doFlow: net change of: " +
    // (postVol - preVol));
    // }

    return !actions.isEmpty();
  }

  private void flowFrom(ILiquidConduit con, List<FlowAction> actions) {

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
      int filled = downCon.fill(ForgeDirection.UP, tank.getFluid().copy(), false, false);
      tank.addAmount(-filled);
      downCon.getTank().addAmount(filled);
    }

    totalAmount = tank.getFluidAmount();
    if (totalAmount <= 0) {
      return;
    }
    FluidStack available = tank.getFluid();
    int totalRequested = 0;
    int numRequests = 0;
    // Then to external connections
    for (ForgeDirection dir : con.getExternalConnections()) {
      if (con.canOutputToDir(dir)) {
        IFluidHandler extCon = con.getExternalHandler(dir);
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

      // -----
      amountPerRequest = Math.min(maxFlowVolume, amountPerRequest);
      // ----

      FluidStack requestSource = available.copy();
      requestSource.amount = amountPerRequest;
      for (ForgeDirection dir : con.getExternalConnections()) {
        if (con.canOutputToDir(dir)) {
          IFluidHandler extCon = con.getExternalHandler(dir);
          if (extCon != null) {
            int amount = extCon.fill(dir.getOpposite(), requestSource.copy(), true);
            if (amount > 0) {
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
    int netChange = 0;
    int flowVolume = Math.round((targetRatio - tank.getFilledRatio()) * tank.getCapacity());

    // -----
    flowVolume = Math.min(maxFlowVolume, flowVolume);
    // ----

    if (Math.abs(flowVolume) < 2) {
      return; // dont bother with transfers of less than a thousands of a bucket
    }

    netChange += flowVolume;
    actions.add(new FlowAction(con, flowVolume));
    for (ILiquidConduit neigbour : connections) {
      if (canFlowTo(con, neigbour)) { // can only flow within same network
        flowVolume = Math.round((targetRatio - neigbour.getTank().getFilledRatio()) * neigbour.getTank().getCapacity());
        if (flowVolume != 0) {
          actions.add(new FlowAction(neigbour, flowVolume));
          netChange += flowVolume;
        }
      }
    }

    if (netChange != 0) {
      actions.add(new FlowAction(con, -netChange));
    }
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
    ILiquidConduit con;
    int amount;

    FlowAction(ILiquidConduit con, int amount) {
      this.con = con;
      this.amount = amount;
    }

    void apply() {
      if (amount != 0) {
        // int preFlow = con.getTank().getAmount();
        con.getTank().addAmount(amount);
        // System.out.println("LiquidConduitNetwork.FlowAction.apply: Con " +
        // con.getLocation() + " went from " + preFlow + " to " +
        // con.getTank().getAmount()
        // + " with transfer amount " + amount);
      }
    }

  }

}
