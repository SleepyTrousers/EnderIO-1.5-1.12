package crazypants.enderio.machines.machine.generator.combustion;

import javax.annotation.Nullable;

import com.enderio.core.common.fluid.SmartTank;

import crazypants.enderio.base.fluid.FluidFuelRegister;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

public class FuelTank extends SmartTank {
  public FuelTank(int capacity) {
    super(capacity);
  }

  @Override
  public boolean canFillFluidType(@Nullable FluidStack resource) {
    if (resource == null) {
      return false;
    }
    final Fluid fluidIn = resource.getFluid();
    if (fluidIn == null) {
      return false;
    }
    return super.canFillFluidType(resource) && FluidFuelRegister.instance.getFuel(fluidIn) != null;
  }
}