package crazypants.enderio.machine.reservoir;

import com.enderio.core.api.common.util.ITankAccess;
import com.enderio.core.common.fluid.FluidWrapper;
import crazypants.enderio.TileEntityEio;
import crazypants.enderio.fluid.SmartTank;
import crazypants.enderio.fluid.SmartTankFluidHandler;
import crazypants.enderio.fluid.SmartTankFluidReservoirHandler;
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Storable
public class TileReservoir extends TileEntityEio implements ITankAccess.IExtendedTankAccess {

  @Store
  SmartTank tank = new SmartTank(FluidRegistry.WATER, Fluid.BUCKET_VOLUME);
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
    for (EnumFacing neighbor : EnumFacing.VALUES) {
      BlockPos pos1 = getPos().offset(neighbor);
      TileEntity tileEntity = world.getTileEntity(pos1);
      if (tileEntity instanceof TileReservoir && ((TileReservoir) tileEntity).tank != null && !seen.contains(tileEntity)) {
        seen.add((TileReservoir) tileEntity);
        got += ((TileReservoir) tileEntity).tank.getFluidAmount();
        if (got >= Fluid.BUCKET_VOLUME * 2) {
          return true;
        }
        for (EnumFacing neighbor2 : EnumFacing.VALUES) {
          BlockPos pos2 = pos1.offset(neighbor2);
          TileEntity tileEntity2 = world.getTileEntity(pos2);
          if (tileEntity2 instanceof TileReservoir && ((TileReservoir) tileEntity2).tank != null && !seen.contains(tileEntity2)) {
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

  private static int IO_MB_TICK = 100;

  protected void doPush() {
    if (tank.getFluidAmount() > 0) {
      for (EnumFacing dir : EnumFacing.VALUES) {
        if (dir != null && tank.getFluidAmount() > 0) {
          if (FluidWrapper.transfer(tank, world, getPos().offset(dir), dir.getOpposite(), IO_MB_TICK) > 0) {
            setTanksDirty();
          }
        }
      }
    }
  }

  protected void doLeak() {
    BlockPos down = getPos().down();
    int max = Math.min(tank.getFluidAmount() / 3, IO_MB_TICK);
    if (max <= 0) {
      max = 2;
    }
    doLeak(down, max);
    for (EnumFacing dir : EnumFacing.Plane.HORIZONTAL) {
      BlockPos pos1 = down.offset(dir);
      doLeak(pos1, max / 2);
    }
  }

  protected void doLeak(BlockPos pos1, int maxAmount) {
    TileEntity tileEntity = world.getTileEntity(pos1);
    if (tileEntity instanceof TileReservoir && !((TileReservoir) tileEntity).tank.isFull()) {
      FluidStack canDrain = tank.drainInternal(maxAmount, false);
      if (canDrain != null && canDrain.amount > 0) {
        int fill = ((TileReservoir) tileEntity).tank.fill(canDrain, true);
        tank.drainInternal(fill, true);
        ((TileReservoir) tileEntity).setTanksDirty();
        setTanksDirty();
      }
    }
  }

  protected void doEqualize() {
    for (EnumFacing dir : EnumFacing.Plane.HORIZONTAL) {
      BlockPos pos1 = getPos().offset(dir);
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
    if (tank == null) {
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
  public FluidTank[] getOutputTanks() {
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
      smartTankFluidHandler = new SmartTankFluidReservoirHandler(this, tank);
    }
    return smartTankFluidHandler;
  }

  @Override
  public boolean hasCapability(Capability<?> capability, EnumFacing facingIn) {
    if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
      return getSmartTankFluidHandler().has(facingIn);
    }
    return super.hasCapability(capability, facingIn);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T getCapability(Capability<T> capability, EnumFacing facingIn) {
    if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
      return (T) getSmartTankFluidHandler().get(facingIn);
    }
    return super.getCapability(capability, facingIn);
  }

}
