package crazypants.enderio.machines.machine.reservoir;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.api.common.util.ITankAccess;
import com.enderio.core.common.fluid.FluidWrapper;
import com.enderio.core.common.fluid.SmartTank;
import com.enderio.core.common.fluid.SmartTankFluidHandler;
import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NNList.NNIterator;

import crazypants.enderio.base.TileEntityEio;
import info.loenwind.autosave.annotations.Storable;
import info.loenwind.autosave.annotations.Store;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

@Storable
public abstract class TileReservoirBase extends TileEntityEio implements ITankAccess.IExtendedTankAccess {

  @Store
  protected final @Nonnull SmartTank tank;
  @Store
  protected boolean autoEject = false;

  protected boolean tankDirty = false;

  public static class TileOmniReservoir extends TileReservoirBase {

    public TileOmniReservoir() {
      super(null);
    }

    @Override
    protected void doInfiniteSource() {
    }

    @Override
    protected boolean allowExtracting() {
      return true;
    }

  }

  public static class TileReservoir extends TileReservoirBase {

    protected boolean canRefill = false;

    public TileReservoir() {
      super(FluidRegistry.WATER);
    }

    @Override
    protected void doInfiniteSource() {
      if (shouldDoWorkThisTick(WORK_TICKS * 2)) {
        if (tankDirty || !tank.isFull() || !canRefill) {
          canRefill = hasEnoughLiquid();
        }
      } else if (canRefill && !tank.isFull() && shouldDoWorkThisTick(WORK_TICKS * 2, -1) && tank.getFluid() != null) {
        tank.addFluidAmount(Fluid.BUCKET_VOLUME / 2);
        setTanksDirty();
      }
    }

    private boolean hasEnoughLiquid() {
      Set<TileReservoirBase> seen = new HashSet<TileReservoirBase>();
      seen.add(this);
      int got = tank.getFluidAmount();
      for (NNIterator<EnumFacing> itr = NNList.FACING.fastIterator(); itr.hasNext();) {
        BlockPos pos1 = getPos().offset(itr.next());
        TileReservoirBase other = BlockReservoirBase.getAnyTileEntity(world, pos1, this.getClass());
        if (other != null && !seen.contains(other)) {
          seen.add(other);
          got += other.tank.getFluidAmount();
          if (got >= Fluid.BUCKET_VOLUME * 2) {
            return true;
          }
          for (NNIterator<EnumFacing> itr2 = NNList.FACING.fastIterator(); itr2.hasNext();) {
            BlockPos pos2 = pos1.offset(itr2.next());
            TileReservoirBase other2 = BlockReservoirBase.getAnyTileEntity(world, pos2, this.getClass());
            if (other2 != null && !seen.contains(other2)) {
              seen.add(other2);
              got += other2.tank.getFluidAmount();
              if (got >= Fluid.BUCKET_VOLUME * 2) {
                return true;
              }
            }
          }
        }
      }
      return false;
    }

    @Override
    protected boolean allowExtracting() {
      return canRefill;
    }

  }

  private TileReservoirBase(@Nullable Fluid fluid) {
    tank = new SmartTank(fluid, Fluid.BUCKET_VOLUME);
    tank.setTileEntity(this);
    addICap(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, facing -> getSmartTankFluidHandler().get(facing));
  }

  private static final int IO_MB_TICK = 100;

  protected void doPush(int ticks) {
    if (!tank.isEmpty()) {
      for (NNIterator<EnumFacing> itr = NNList.FACING.fastIterator(); itr.hasNext() && !tank.isEmpty();) {
        final EnumFacing dir = itr.next();
        final BlockPos neighbor = getPos().offset(dir);
        if (world.getBlockState(neighbor).getBlock() != blockType && FluidWrapper.transfer(tank, world, neighbor, dir.getOpposite(), IO_MB_TICK * ticks) > 0) {
          setTanksDirty();
        }
      }
    }
  }

  /**
   * Leak fluid to blocks below
   */
  protected void doLeak() {
    BlockPos down = getPos().down();
    if (doLeak(down)) {
      for (NNIterator<EnumFacing> itr = NNList.FACING_HORIZONTAL.fastIterator(); itr.hasNext() && !tank.isEmpty(); doLeak(down.offset(itr.next()))) {
      }
    }
  }

  /**
   * Leak fluid to other blocks (which should be below the current one).
   * 
   * @param otherPos
   * @return true if the target block was a reservoir. False otherwise.
   */
  protected boolean doLeak(@Nonnull BlockPos otherPos) {
    TileReservoirBase other = BlockReservoirBase.getAnyTileEntity(world, otherPos, this.getClass());
    if (other != null) {
      if (!other.tank.isFull() && other.tank.canFillFluidType(tank.getFluidNN())) {
        FluidStack canDrain = tank.drainInternal(other.tank.getAvailableSpace(), false);
        if (canDrain != null && canDrain.amount > 0) {
          int fill = other.tank.fill(canDrain, true);
          tank.drainInternal(fill, true);
          other.setTanksDirty();
          setTanksDirty();
        }
      }
      return true;
    }
    return false;
  }

  /**
   * Equalize fluid levels with neighbors
   */
  protected void doEqualize() {
    for (NNIterator<EnumFacing> itr = NNList.FACING_HORIZONTAL.fastIterator(); itr.hasNext() && !tank.isEmpty();) {
      BlockPos pos1 = getPos().offset(itr.next());
      TileReservoirBase other = BlockReservoirBase.getAnyTileEntity(world, pos1, this.getClass());
      if (other != null) {
        int toMove = (tank.getFluidAmount() - other.tank.getFluidAmount()) / 2;
        if (toMove > 0) {
          FluidStack canDrain = tank.drainInternal(Math.min(toMove, IO_MB_TICK / 4), false);
          if (canDrain != null && canDrain.amount > 0) {
            int fill = other.tank.fill(canDrain, true);
            tank.drainInternal(fill, true);
            other.setTanksDirty();
            setTanksDirty();
          }
        }
      }
    }
  }

  protected static final int WORK_TICKS = 5;

  @Override
  public void doUpdate() {
    if (world.isRemote) {
      disableTicking();
      return;
    }

    doInfiniteSource();

    if (shouldDoWorkThisTick(WORK_TICKS * 3, 1) && !tank.isEmpty()) {
      doLeak();
      if (!tank.isEmpty()) {
        doEqualize();
      }
    }
    if (shouldDoWorkThisTick(WORK_TICKS)) {
      if (autoEject && allowExtracting()) {
        doPush(WORK_TICKS);
      }

      if (tankDirty) {
        forceUpdatePlayers();
        tankDirty = false;
      }
    }
  }

  protected abstract void doInfiniteSource();

  protected abstract boolean allowExtracting();

  public void setAutoEject(boolean autoEject) {
    this.autoEject = autoEject;
    setTanksDirty(); // force client update and data saving
  }

  public boolean isAutoEject() {
    return autoEject;
  }

  float getFilledRatio() {
    return tank.getFilledRatio();
  }

  @Override
  public FluidTank getInputTank(FluidStack forFluidType) {
    if (tank.canFillFluidType(forFluidType)) {
      return tank;
    }
    return null;
  }

  @Override
  public @Nonnull FluidTank[] getOutputTanks() {
    return new FluidTank[] { tank };
  }

  @Override
  public void setTanksDirty() {
    if (!tankDirty) {
      tankDirty = true;
      markDirty();
    }
  }

  @Override
  public boolean shouldRenderInPass(int pass) {
    return pass == 1 && !tank.isEmpty();
  }

  @SuppressWarnings("null")
  @Override
  @Nonnull
  public List<ITankData> getTankDisplayData() {
    return Collections.<ITankData> singletonList(new ITankData() {

      @Override
      @Nonnull
      public EnumTankType getTankType() {
        return EnumTankType.OUTPUT;
      }

      @Override
      @Nullable
      public FluidStack getContent() {
        return tank.getFluid();
      }

      @Override
      public int getCapacity() {
        return tank.getCapacity();
      }
    });
  }

  private SmartTankFluidHandler smartTankFluidHandler;

  protected SmartTankFluidHandler getSmartTankFluidHandler() {
    if (smartTankFluidHandler == null) {
      smartTankFluidHandler = new SmartTankFluidHandler(tank) {

        @Override
        protected boolean canFill(@Nonnull EnumFacing from) {
          return true;
        }

        @Override
        protected boolean canDrain(@Nonnull EnumFacing from) {
          return allowExtracting();
        }

        @Override
        protected boolean canAccess(@Nonnull EnumFacing from) {
          return true;
        }

      };
    }
    return smartTankFluidHandler;
  }

  protected @Nonnull SmartTank getTank() {
    return tank;
  }

  @Override
  public boolean hasFastRenderer() {
    return true;
  }

}
