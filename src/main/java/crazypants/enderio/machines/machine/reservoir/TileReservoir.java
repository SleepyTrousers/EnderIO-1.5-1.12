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
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

@Storable
public class TileReservoir extends TileEntityEio implements ITankAccess.IExtendedTankAccess {

  @Store
  final @Nonnull SmartTank tank = new SmartTank(FluidRegistry.WATER, Fluid.BUCKET_VOLUME);
  public boolean canRefill = false;

  @Store
  boolean autoEject = false;

  private boolean tankDirty = false;

  public TileReservoir() {
    super();
    tank.setTileEntity(this);
  }

  private boolean hasEnoughLiquid() {
    Set<TileReservoir> seen = new HashSet<TileReservoir>();
    seen.add(this);
    int got = tank.getFluidAmount();
    for (NNIterator<EnumFacing> itr = NNList.FACING.fastIterator(); itr.hasNext();) {
      BlockPos pos1 = getPos().offset(itr.next());
      TileEntity tileEntity = world.getTileEntity(pos1);
      if (tileEntity instanceof TileReservoir && !seen.contains(tileEntity)) {
        seen.add((TileReservoir) tileEntity);
        got += ((TileReservoir) tileEntity).tank.getFluidAmount();
        if (got >= Fluid.BUCKET_VOLUME * 2) {
          return true;
        }
        for (NNIterator<EnumFacing> itr2 = NNList.FACING.fastIterator(); itr2.hasNext();) {
          BlockPos pos2 = pos1.offset(itr2.next());
          TileEntity tileEntity2 = world.getTileEntity(pos2);
          if (tileEntity2 instanceof TileReservoir && !seen.contains(tileEntity2)) {
            seen.add((TileReservoir) tileEntity2);
            got += ((TileReservoir) tileEntity2).tank.getFluidAmount();
            if (got >= Fluid.BUCKET_VOLUME * 2) {
              return true;
            }
          }
        }
      }
    }
    return false;
  }

  private static final int IO_MB_TICK = 100;

  protected void doPush() {
    if (!tank.isEmpty()) {
      for (NNIterator<EnumFacing> itr = NNList.FACING.fastIterator(); itr.hasNext() && !tank.isEmpty();) {
        final EnumFacing dir = itr.next();
        final BlockPos neighbor = getPos().offset(dir);
        if (world.getBlockState(neighbor).getBlock() != blockType && FluidWrapper.transfer(tank, world, neighbor, dir.getOpposite(), IO_MB_TICK) > 0) {
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
    TileEntity tileEntity = world.getTileEntity(otherPos);
    if (tileEntity instanceof TileReservoir) {
      final TileReservoir otherTe = (TileReservoir) tileEntity;
      if (!otherTe.tank.isFull()) {
        FluidStack canDrain = tank.drainInternal(otherTe.tank.getAvailableSpace(), false);
        if (canDrain != null && canDrain.amount > 0) {
          int fill = otherTe.tank.fill(canDrain, true);
          tank.drainInternal(fill, true);
          otherTe.setTanksDirty();
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
      TileEntity tileEntity = world.getTileEntity(pos1);
      if (tileEntity instanceof TileReservoir) {
        TileReservoir other = (TileReservoir) tileEntity;
        int toMove = (tank.getFluidAmount() - other.tank.getFluidAmount()) / 2;
        if (toMove > 0) {
          FluidStack canDrain = tank.drainInternal(Math.min(toMove, IO_MB_TICK / 4), false);
          if (canDrain != null && canDrain.amount > 0) {
            int fill = ((TileReservoir) tileEntity).tank.fill(canDrain, true);
            tank.drainInternal(fill, true);
            ((TileReservoir) tileEntity).setTanksDirty();
            setTanksDirty();
          }
        }
      }
    }
  }

  @Override
  public void doUpdate() {
    if (world.isRemote) {
      super.doUpdate(); // disable ticking on the client
      return;
    }

    if (shouldDoWorkThisTick(10)) {
      if (tankDirty || !tank.isFull() || !canRefill) {
        canRefill = hasEnoughLiquid();
      }
    } else if (canRefill && !tank.isFull() && shouldDoWorkThisTick(10, -1)) {
      tank.addFluidAmount(Fluid.BUCKET_VOLUME / 2);
      setTanksDirty();
    }

    if (shouldDoWorkThisTick(15, 1) && !tank.isEmpty()) {
      doLeak();
      if (!tank.isEmpty()) {
        doEqualize();
      }
    }
    if (autoEject && canRefill) {
      doPush();
    }

    if (tankDirty && shouldDoWorkThisTick(2)) {
      updateBlock();
      tankDirty = false;
    }
  }

  public void setAutoEject(boolean autoEject) {
    this.autoEject = autoEject;
  }

  public boolean isAutoEject() {
    return autoEject;
  }

  float getFilledRatio() {
    return tank.getFilledRatio();
  }

  @Override
  public FluidTank getInputTank(FluidStack forFluidType) {
    if (forFluidType != null && forFluidType.getFluid() == FluidRegistry.WATER) {
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
          return TileReservoir.this.canRefill;
        }

        @Override
        protected boolean canAccess(@Nonnull EnumFacing from) {
          return true;
        }

      };
    }
    return smartTankFluidHandler;
  }

  @Override
  public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facingIn) {
    if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
      return getSmartTankFluidHandler().has(facingIn);
    }
    return super.hasCapability(capability, facingIn);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facingIn) {
    if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
      return (T) getSmartTankFluidHandler().get(facingIn);
    }
    return super.getCapability(capability, facingIn);
  }

}
