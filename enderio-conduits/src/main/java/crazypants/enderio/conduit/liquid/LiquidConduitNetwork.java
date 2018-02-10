package crazypants.enderio.conduit.liquid;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;

import com.enderio.core.common.fluid.IFluidWrapper;

import crazypants.enderio.base.conduit.ConduitUtil;
import net.minecraft.profiler.Profiler;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;

public class LiquidConduitNetwork extends AbstractTankConduitNetwork<LiquidConduit> {

  public LiquidConduitNetwork() {
    super(LiquidConduit.class);
  }

  private int ticksEmpty = 0;

  private int maxFlowsPerTick = 10;
  private int lastFlowIndex = 0;

  private int lastPushToken = 0;

  private boolean inputLocked = false;

  public boolean lockNetworkForFill() {
    if (inputLocked) {
      return false;
    }
    inputLocked = true;
    return true;
  }

  public void unlockNetworkFromFill() {
    inputLocked = false;
  }

  @Override
  public void tickEnd(ServerTickEvent event, Profiler profiler) {
    List<LiquidConduit> cons = getConduits();
    if (cons.isEmpty()) {
      return;
    }

    if (isEmpty()) {
      if (!fluidTypeLocked && liquidType != null) {
        ticksEmpty++;
        if (ticksEmpty > 40) {
          setFluidType(null);
          ticksEmpty = 0;
        }
      }
      return;
    }

    ticksEmpty = 0;
    long curTime = cons.get(0).getBundle().getEntity().getWorld().getTotalWorldTime();

    // 1000 water, 6000 lava
    if (liquidType != null && liquidType.getFluid() != null && !isEmpty()) {
      int visc = Math.max(1000, liquidType.getFluid().getViscosity());
      if (curTime % (visc / 500) == 0) {
        profiler.startSection("flow");
        doFlow();
        profiler.endSection();
      }
    }
  }

  void addedFromExternal(int res) {
  }

  void outputedToExternal(int filled) {
  }

  int getNextPushToken() {
    return ++lastPushToken;
  }

  private boolean doFlow() {

    int pushToken = getNextPushToken();
    List<FlowAction> actions = new ArrayList<FlowAction>();
    for (int i = 0; i < Math.min(maxFlowsPerTick, getConduits().size()); i++) {

      if (lastFlowIndex >= getConduits().size()) {
        lastFlowIndex = 0;
      }
      flowFrom(getConduits().get(lastFlowIndex), actions, pushToken);
      ++lastFlowIndex;

    }
    for (FlowAction action : actions) {
      action.apply();
    }

    boolean result = !actions.isEmpty();

    // Flush any tanks with a tiny bit left
    List<LiquidConduit> toEmpty = new ArrayList<LiquidConduit>();
    for (LiquidConduit con : getConduits()) {
      if (con != null && con.getTank().getFluidAmount() < 10) {
        toEmpty.add(con);
      } else {
        // some of the conduits have fluid left in them so don't do the final drain yet
        return result;
      }

    }
    if (toEmpty.isEmpty()) {
      return result;
    }

    List<LocatedFluidHandler> externals = new ArrayList<LocatedFluidHandler>();
    for (AbstractTankConduit con : getConduits()) {
      Set<EnumFacing> extCons = con.getExternalConnections();

      for (EnumFacing dir : extCons) {
        if (con.canOutputToDir(dir)) {
          IFluidWrapper externalTank = con.getExternalHandler(dir);
          if (externalTank != null) {
            externals.add(new LocatedFluidHandler(externalTank, con.getBundle().getLocation().offset(dir), dir.getOpposite()));
          }
        }
      }
    }
    if (externals.isEmpty()) {
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
    if (!fluidTypeLocked && isEmpty()) {
      setFluidType(null);
    }
  }

  private boolean isEmpty() {
    for (LiquidConduit con : getConduits()) {
      if (con.tank.getFluidAmount() > 0) {
        return false;
      }
    }
    return true;
  }

  private void drainConduitToNearestExternal(@Nonnull LiquidConduit con, List<LocatedFluidHandler> externals) {
    BlockPos conPos = con.getBundle().getLocation();
    FluidStack toDrain = con.getTank().getFluid();
    if (toDrain == null) {
      return;
    }
    int closestDistance = Integer.MAX_VALUE;
    LocatedFluidHandler closestTank = null;
    for (LocatedFluidHandler lh : externals) {
      int distance = (int) lh.pos.distanceSq(conPos);
      if (distance < closestDistance && con.canOutputToDir(lh.dir.getOpposite())) {
        int couldFill = lh.tank.offer(toDrain.copy());
        if (couldFill > 0) {
          closestTank = lh;
          closestDistance = distance;
        }
      }
    }

    if (closestTank != null) {
      int filled = closestTank.tank.fill(toDrain.copy());
      con.getTank().addAmount(-filled);
    }

  }

  private void flowFrom(@Nonnull LiquidConduit con, List<FlowAction> actions, int pushPoken) {

    ConduitTank tank = con.getTank();
    int totalAmount = tank.getFluidAmount();
    if (totalAmount <= 0) {
      return;
    }

    int maxFlowVolume = 20;

    // First flow all we can down, then balance the rest
    if (con.getConduitConnections().contains(EnumFacing.DOWN)) {
      BlockPos pos = con.getBundle().getLocation().offset(EnumFacing.DOWN);
      ILiquidConduit dc = ConduitUtil.getConduit(con.getBundle().getEntity().getWorld(), pos.getX(), pos.getY(), pos.getZ(), ILiquidConduit.class);
      if (dc instanceof LiquidConduit) {
        LiquidConduit downCon = (LiquidConduit) dc;
        int filled = downCon.fill(EnumFacing.UP, tank.getFluid().copy(), false, false, pushPoken);
        int actual = filled;
        actual = Math.min(actual, tank.getFluidAmount());
        actual = Math.min(actual, downCon.getTank().getAvailableSpace());
        tank.addAmount(-actual);
        downCon.getTank().addAmount(actual);
      }
    }

    totalAmount = tank.getFluidAmount();
    if (totalAmount <= 0) {
      return;
    }
    FluidStack available = tank.getFluid();
    int totalRequested = 0;
    int numRequests = 0;
    // Then to external connections
    for (EnumFacing dir : con.getExternalConnections()) {
      if (con.canOutputToDir(dir)) {
        IFluidWrapper extCon = con.getExternalHandler(dir);
        if (extCon != null) {
          int amount = extCon.offer(available.copy());
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

      FluidStack requestSource = available.copy();
      requestSource.amount = amountPerRequest;
      for (EnumFacing dir : con.getExternalConnections()) {
        if (con.canOutputToDir(dir)) {
          IFluidWrapper extCon = con.getExternalHandler(dir);
          if (extCon != null) {
            int amount = extCon.fill(requestSource.copy());
            if (amount > 0) {
              outputedToExternal(amount);
              tank.addAmount(-amount);
            }
          }
        }
      }
    }

    totalAmount = tank.getFluidAmount();
    if (totalAmount <= 0) {
      return;
    }
    int totalCapacity = tank.getCapacity();

    BlockPos pos = con.getBundle().getLocation();
    Collection<ILiquidConduit> connections = ConduitUtil.getConnectedConduits(con.getBundle().getEntity().getWorld(), pos.getX(), pos.getY(), pos.getZ(),
        ILiquidConduit.class);
    for (ILiquidConduit n : connections) {
      LiquidConduit neighbour = (LiquidConduit) n;
      if (canFlowTo(con, neighbour)) { // can only flow within same network
        totalAmount += neighbour.getTank().getFluidAmount();
        totalCapacity += neighbour.getTank().getCapacity();
      }
    }

    float targetRatio = (float) totalAmount / totalCapacity;
    int flowVolume = (int) Math.floor((targetRatio - tank.getFilledRatio()) * tank.getCapacity());
    flowVolume = Math.min(maxFlowVolume, flowVolume);

    if (Math.abs(flowVolume) < 2) {
      return; // dont bother with transfers of less than a thousands of a bucket
    }

    for (ILiquidConduit n : connections) {
      LiquidConduit neigbour = (LiquidConduit) n;
      if (canFlowTo(con, neigbour)) { // can only flow within same network
        flowVolume = (int) Math.floor((targetRatio - neigbour.getTank().getFilledRatio()) * neigbour.getTank().getCapacity());
        if (flowVolume != 0) {
          actions.add(new FlowAction(con, neigbour, flowVolume));
        }
      }
    }

  }

  private boolean canFlowTo(LiquidConduit con, LiquidConduit neighbour) {
    if (con == null || neighbour == null) {
      return false;
    }
    if (neighbour.getNetwork() != this) {
      return false;
    }
    if (neighbour.getBundle().getLocation().getY() > con.getBundle().getLocation().getY()) {
      return false;
    }
    float nr = neighbour.getTank().getFilledRatio();
    if (nr >= con.getTank().getFilledRatio()) {
      return false;
    }
    return true;
  }

  static class FlowAction {
    final LiquidConduit from;
    final LiquidConduit to;
    final int amount;

    FlowAction(LiquidConduit fromIn, LiquidConduit toIn, int amountIn) {
      if (amountIn < 0) {
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

        // don't take more than it has
        int actual = Math.min(amount, from.getTank().getFluidAmount());
        // and don't add more than it can take
        actual = Math.min(actual, to.getTank().getAvailableSpace());

        if (from != null && to != null) {
          from.getTank().addAmount(-actual);
          to.getTank().addAmount(actual);
        }

      }
    }

  }

  static class LocatedFluidHandler {
    final IFluidWrapper tank;
    final BlockPos pos;
    final EnumFacing dir;

    LocatedFluidHandler(IFluidWrapper tank, BlockPos pos, EnumFacing dir) {
      this.tank = tank;
      this.pos = pos;
      this.dir = dir;
    }
  }

}
