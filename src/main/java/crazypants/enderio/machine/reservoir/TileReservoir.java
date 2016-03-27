package crazypants.enderio.machine.reservoir;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

import com.enderio.core.api.common.util.ITankAccess;
import com.enderio.core.common.util.FluidUtil;

import crazypants.enderio.TileEntityEio;
import crazypants.enderio.tool.SmartTank;

import static net.minecraftforge.fluids.FluidContainerRegistry.BUCKET_VOLUME;

public class TileReservoir extends TileEntityEio implements IFluidHandler, ITankAccess {

  SmartTank tank = new SmartTank(FluidRegistry.WATER, BUCKET_VOLUME);
  private boolean canRefill = false;

  boolean autoEject = false;

  private boolean tankDirty = false;

  private boolean hasEnoughLiquid() {
    Set<TileReservoir> seen = new HashSet<TileReservoir>();
    seen.add(this);
    int got = tank.getFluidAmount();
    for (EnumFacing neighbor : EnumFacing.VALUES) {
      BlockPos pos1 = getPos().offset(neighbor);
      TileEntity tileEntity = worldObj.getTileEntity(pos1);
      if (tileEntity instanceof TileReservoir && ((TileReservoir) tileEntity).tank != null && !seen.contains(tileEntity)) {
        seen.add((TileReservoir) tileEntity);
        got += ((TileReservoir) tileEntity).tank.getFluidAmount();
        if (got >= BUCKET_VOLUME * 2) {
          return true;
        }
        for (EnumFacing neighbor2 : EnumFacing.VALUES) {
          BlockPos pos2 = pos1.offset(neighbor2);
          TileEntity tileEntity2 = worldObj.getTileEntity(pos2);
          if (tileEntity2 instanceof TileReservoir && ((TileReservoir) tileEntity2).tank != null && !seen.contains(tileEntity2)) {
            seen.add((TileReservoir) tileEntity2);
            got += ((TileReservoir) tileEntity2).tank.getFluidAmount();
            if (got >= BUCKET_VOLUME * 2) {
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
        BlockPos loc = getPos().offset(dir);
        IFluidHandler target = FluidUtil.getFluidHandler(worldObj, loc.getX(), loc.getY(), loc.getZ());
        if (target != null && !(target instanceof TileReservoir)) {
          if (target.canFill(dir.getOpposite(), tank.getFluid().getFluid())) {
            FluidStack push = tank.getFluid().copy();
            push.amount = Math.min(push.amount, IO_MB_TICK);
            int filled = target.fill(dir.getOpposite(), push, true);
            if (filled > 0) {
              tank.drain(filled, true);
              tankDirty = true;
              if (tank.getFluidAmount() == 0) {
                return;
              }
            }
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
    TileEntity tileEntity = worldObj.getTileEntity(pos1);
    if (tileEntity instanceof TileReservoir && !((TileReservoir) tileEntity).tank.isFull()) {
      FluidStack canDrain = tank.drain(maxAmount, false);
      if (canDrain != null && canDrain.amount > 0) {
        int fill = ((TileReservoir) tileEntity).tank.fill(canDrain, true);
        tank.drain(fill, true);
        ((TileReservoir) tileEntity).tankDirty = tankDirty = true;
      }
    }
  }

  protected void doEqualize() {
    for (EnumFacing dir : EnumFacing.Plane.HORIZONTAL) {
      BlockPos pos1 = getPos().offset(dir);
      TileEntity tileEntity = worldObj.getTileEntity(pos1);
      if (tileEntity instanceof TileReservoir) {
        TileReservoir other = (TileReservoir) tileEntity;
        int toMove = (tank.getFluidAmount() - other.tank.getFluidAmount()) / 2;
        if (toMove > 0) {
          FluidStack canDrain = tank.drain(Math.min(toMove, IO_MB_TICK / 4), false);
          if (canDrain != null && canDrain.amount > 0) {
            int fill = ((TileReservoir) tileEntity).tank.fill(canDrain, true);
            tank.drain(fill, true);
            ((TileReservoir) tileEntity).tankDirty = tankDirty = true;
          }
        }
      }
    }
  }

  @Override
  public void doUpdate() {
    if (worldObj.isRemote || tank == null) {
      return;
    }

    if (shouldDoWorkThisTick(10)) {
      canRefill = tank.isFull() ? canRefill || hasEnoughLiquid() : hasEnoughLiquid();
    } else if (shouldDoWorkThisTick(10, -1) && canRefill) {
      tank.addFluidAmount(BUCKET_VOLUME / 2);
      tankDirty = true;
    }

    if (shouldDoWorkThisTick(15, 1) && tank.getFluidAmount() > 0) {
      doLeak();
      if (tank.getFluidAmount() > 0) {
        doEqualize();
      }
    }
    if (autoEject && canRefill) {
      doPush();
    }

    if (tankDirty && shouldDoWorkThisTick(2)) {
      worldObj.markBlockForUpdate(getPos());
      tankDirty = false;
    }
  }

  @Override
  public int fill(EnumFacing from, FluidStack resource, boolean doFill) {
    int ret = tank.fill(resource, doFill);
    tankDirty |= doFill && ret != 0;
    return ret;
  }

  @Override
  public FluidStack drain(EnumFacing from, int maxDrain, boolean doDrain) {
    if (canRefill) {
      FluidStack ret = tank.drain(maxDrain, doDrain);
      tankDirty |= doDrain && ret != null && ret.amount != 0;
      return ret;
    } else {
      return null;
    }
  }

  @Override
  public FluidStack drain(EnumFacing from, FluidStack resource, boolean doDrain) {
    if (resource == null || !resource.isFluidEqual(tank.getFluid())) {
      return null;
    }
    return drain(from, resource.amount, doDrain);
  }

  @Override
  public boolean canFill(EnumFacing from, Fluid fluid) {
    return fluid == FluidRegistry.WATER;
  }

  @Override
  public boolean canDrain(EnumFacing from, Fluid fluid) {
    return fluid == FluidRegistry.WATER;
  }

  @Override
  public FluidTankInfo[] getTankInfo(EnumFacing from) {
    return new FluidTankInfo[] { tank.getInfo() };
  }

  public void setAutoEject(boolean autoEject) {
    this.autoEject = autoEject;
  }

  public boolean isAutoEject() {
    return autoEject;
  }

  @Override
  public void readCustomNBT(NBTTagCompound nbtRoot) {
    autoEject = nbtRoot.getBoolean("autoEject");

    FluidStack liquid = FluidStack.loadFluidStackFromNBT(nbtRoot.getCompoundTag("tank"));

    if (liquid != null) {
      tank.setFluid(liquid);
    } else {
      tank.setFluidAmount(0);
    }
  }

  @Override
  public void writeCustomNBT(NBTTagCompound nbtRoot) {
    nbtRoot.setBoolean("autoEject", autoEject);

    if (tank.getFluid() != null && FluidRegistry.getFluidName(tank.getFluid()) != null) {
      nbtRoot.setTag("tank", tank.getFluid().writeToNBT(new NBTTagCompound()));
    }
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
    tankDirty = true;
  }

}
