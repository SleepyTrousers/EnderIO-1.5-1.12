package crazypants.enderio.machines.machine.tank;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.fluid.SmartTank;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

class TankItemFluidCapability implements IFluidHandlerItem, ICapabilityProvider {
  protected final @Nonnull ItemStack container;

  TankItemFluidCapability(@Nonnull ItemStack container) {
    this.container = container;
  }

  @Override
  public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
    return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
    return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY ? (T) this : null;
  }

  @Override
  public IFluidTankProperties[] getTankProperties() {
    return EnumTankType.loadTank(container).getTankProperties();
  }

  @Override
  public int fill(FluidStack resource, boolean doFill) {
    if (container.getCount() != 1) {
      return 0;
    }
    SmartTank tank = EnumTankType.loadTank(container);
    int ret = tank.fill(resource, doFill);
    EnumTankType.saveTank(container, tank);
    return ret;
  }

  @Override
  @Nullable
  public FluidStack drain(FluidStack resource, boolean doDrain) {
    if (container.getCount() != 1) {
      return null;
    }
    SmartTank tank = EnumTankType.loadTank(container);
    FluidStack ret = tank.drain(resource, doDrain);
    EnumTankType.saveTank(container, tank);
    return ret;
  }

  @Override
  @Nullable
  public FluidStack drain(int maxDrain, boolean doDrain) {
    if (container.getCount() != 1) {
      return null;
    }
    SmartTank tank = EnumTankType.loadTank(container);
    FluidStack ret = tank.drain(maxDrain, doDrain);
    EnumTankType.saveTank(container, tank);
    return ret;
  }

  @Override
  public @Nonnull ItemStack getContainer() {
    return container;
  }

}