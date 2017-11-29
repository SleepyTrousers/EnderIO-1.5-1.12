package crazypants.enderio.base.fluid;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

public interface IFluidRegister {

  @Nullable
  IFluidFuel getFuel(@Nonnull FluidStack fluid);

  @Nullable
  IFluidFuel getFuel(@Nonnull Fluid fluid);

  void addFuel(@Nonnull Fluid fluid, int powerPerCycleRF, int totalBurnTime);

  @Nullable
  IFluidCoolant getCoolant(@Nonnull FluidStack fluid);

  @Nullable
  IFluidCoolant getCoolant(@Nonnull Fluid fluid);

  void addCoolant(@Nonnull Fluid fluid, float degreesCoolingPerMB);

}
