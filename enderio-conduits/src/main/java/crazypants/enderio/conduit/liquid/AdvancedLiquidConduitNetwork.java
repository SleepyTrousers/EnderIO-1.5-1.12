package crazypants.enderio.conduit.liquid;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.annotation.Nonnull;

import com.enderio.core.common.fluid.FluidWrapper;
import com.enderio.core.common.fluid.IFluidWrapper;

import crazypants.enderio.base.conduit.IConduit;
import net.minecraft.profiler.Profiler;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

// TODO Javadocs
public class AdvancedLiquidConduitNetwork extends AbstractTankConduitNetwork<AdvancedLiquidConduit> {

  private final ConduitTank tank = new ConduitTank(0);

  private final Set<LiquidOutput> outputs = new HashSet<LiquidOutput>();

  private Iterator<LiquidOutput> outputIterator;

  private boolean lastSyncedActive = false;

  private int lastSyncedVolume = -1;

  private int ticksEmpty;

  public AdvancedLiquidConduitNetwork() {
    super(AdvancedLiquidConduit.class);
  }

  @Override
  public void addConduit(@Nonnull AdvancedLiquidConduit con) {
    tank.setCapacity(tank.getCapacity() + AdvancedLiquidConduit.CONDUIT_VOLUME);
    if (con.getTank().containsValidLiquid()) {
      tank.addAmount(con.getTank().getFluidAmount());
    }
    for (EnumFacing dir : con.getExternalConnections()) {
      if (con.getConnectionMode(dir).acceptsOutput()) {
        outputs.add(new LiquidOutput(con.getBundle().getLocation().offset(dir), dir.getOpposite()));
      }
    }
    outputIterator = null;
    super.addConduit(con);
  }

  @Override
  public boolean setFluidType(FluidStack newType) {
    if (super.setFluidType(newType)) {

      FluidStack ft = getFluidType();
      tank.setLiquid(ft == null ? null : ft.copy());
      return true;
    }
    return false;
  }

  @Override
  public void setFluidTypeLocked(boolean fluidTypeLocked) {
    super.setFluidTypeLocked(fluidTypeLocked);
    if (!fluidTypeLocked && tank.isEmpty()) {
      setFluidType(null);
    }
  }

  @Override
  public void destroyNetwork() {
    setConduitVolumes();
    outputs.clear();
    super.destroyNetwork();
  }

  private void setConduitVolumes() {
    if (tank.containsValidLiquid() && !getConduits().isEmpty()) {
      FluidStack fluidPerConduit = tank.getFluid().copy();
      int numCons = getConduits().size();
      int leftOvers = fluidPerConduit.amount % numCons;
      fluidPerConduit.amount = fluidPerConduit.amount / numCons;

      for (AdvancedLiquidConduit con : getConduits()) {
        FluidStack f = fluidPerConduit.copy();
        if (leftOvers > 0) {
          f.amount += 1;
          leftOvers--;
        }
        con.getTank().setLiquid(f);
        BlockPos pos = con.getBundle().getLocation();
        con.getBundle().getEntity().getWorld().markChunkDirty(pos, con.getBundle().getEntity());
      }

    }
  }

  @Override
  public void doNetworkTick(@Nonnull Profiler theProfiler) {
    if (liquidType == null || outputs.isEmpty() || !tank.containsValidLiquid() || tank.isEmpty()) {
      theProfiler.startSection("updateActiveState");
      updateActiveState();
      theProfiler.endSection();
      return;
    }

    if (outputIterator == null || !outputIterator.hasNext()) {
      outputIterator = outputs.iterator();
    }

    theProfiler.startSection("updateActiveState");
    updateActiveState();

    theProfiler.endStartSection("pushFluid");
    int numVisited = 0;
    while (!tank.isEmpty() && numVisited < outputs.size()) {
      if (!outputIterator.hasNext()) {
        outputIterator = outputs.iterator();
      }
      LiquidOutput output = outputIterator.next();
      if (output != null) {
        theProfiler.startSection("otherMod_getTankContainer");
        IFluidWrapper cont = getTankContainer(output);
        theProfiler.endSection();
        if (cont != null) {
          FluidStack offer = tank.getFluid().copy();
          theProfiler.startSection("otherMod_fill");
          int filled = cont.fill(offer);
          theProfiler.endSection();
          if (filled > 0) {
            tank.addAmount(-filled);

          }
        }
      }
      numVisited++;
    }

    theProfiler.endSection();
  }

  private void updateActiveState() {

    boolean isActive = tank.containsValidLiquid() && !tank.isEmpty();
    if (!isActive) {
      if (!fluidTypeLocked && liquidType != null) {
        ticksEmpty++;
        if (ticksEmpty > 40) {
          setFluidType(null);
          ticksEmpty = 0;
          for (IConduit con : getConduits()) {
            con.setActive(false);
          }
          lastSyncedActive = false;
        }
      }
      return;
    }

    ticksEmpty = 0;

    if (!lastSyncedActive) {
      for (IConduit con : getConduits()) {
        con.setActive(true);
      }
      lastSyncedActive = true;
    }

  }

  public int fill(FluidStack resource, boolean doFill) {
    if (resource == null) {
      return 0;
    }
    resource.amount = Math.min(resource.amount, AdvancedLiquidConduit.MAX_IO_PER_TICK);
    boolean liquidWasValid = !tank.containsValidLiquid();
    int res = tank.fill(resource, doFill);
    if (doFill && res > 0 && !liquidWasValid) {
      int vol = tank.getFluidAmount();
      setFluidType(resource);
      tank.setAmount(vol);
    }
    return res;
  }

  public FluidStack drain(FluidStack resource, boolean doDrain) {
    if (resource == null || tank.isEmpty() || !tank.containsValidLiquid() || !LiquidConduitNetwork.areFluidsCompatable(getFluidType(), resource)) {
      return null;
    }
    int amount = Math.min(resource.amount, tank.getFluidAmount());
    amount = Math.min(amount, AdvancedLiquidConduit.MAX_IO_PER_TICK);
    FluidStack result = resource.copy();
    result.amount = amount;
    if (doDrain) {
      tank.addAmount(-amount);
    }
    return result;
  }

  public FluidStack drain(int maxDrain, boolean doDrain) {
    if (tank.isEmpty() || !tank.containsValidLiquid()) {
      return null;
    }
    int amount = Math.min(maxDrain, tank.getFluidAmount());
    FluidStack result = tank.getFluid().copy();
    result.amount = amount;
    if (doDrain) {
      tank.addAmount(-amount);
    }
    return result;
  }

  public boolean extractFrom(@Nonnull AdvancedLiquidConduit advancedLiquidConduit, @Nonnull EnumFacing dir, int maxExtractPerTick) {
    if (tank.isFull()) {
      return false;
    }

    IFluidWrapper extTank = getTankContainer(advancedLiquidConduit, dir);
    if (extTank != null) {
      int maxExtract = Math.min(maxExtractPerTick, tank.getAvailableSpace());

      if (liquidType == null || !tank.containsValidLiquid()) {
        FluidStack available = extTank.getAvailableFluid();
        if (available == null || available.amount <= 0) {
          return false;
        }
        setFluidType(available);
      }

      FluidStack couldDrain = liquidType.copy();
      couldDrain.amount = maxExtract;

      FluidStack drained = extTank.drain(couldDrain);
      if (drained == null || drained.amount == 0) {
        return false;
      } else if (drained.isFluidEqual(getFluidType())) {
        tank.addAmount(drained.amount);
      } else {
        extTank.fill(drained);
      }
      return true;
    }
    return false;
  }

  public IFluidWrapper getTankContainer(LiquidOutput output) {
    return FluidWrapper.wrap(getWorld(), output.location, output.dir);
  }

  public IFluidWrapper getTankContainer(@Nonnull AdvancedLiquidConduit con, @Nonnull EnumFacing dir) {
    return FluidWrapper.wrap(getWorld(), con.getBundle().getLocation().offset(dir), dir.getOpposite());
  }

  World getWorld() {
    if (getConduits().isEmpty()) {
      return null;
    }
    return getConduits().get(0).getBundle().getBundleworld();
  }

  public void removeInput(LiquidOutput lo) {
    outputs.remove(lo);
    outputIterator = null;
  }

  public void addInput(LiquidOutput lo) {
    outputs.add(lo);
    outputIterator = null;
  }

  public void updateConduitVolumes() {
    if (tank.getFluidAmount() == lastSyncedVolume) {
      return;
    }
    setConduitVolumes();
    lastSyncedVolume = tank.getFluidAmount();
  }
}
