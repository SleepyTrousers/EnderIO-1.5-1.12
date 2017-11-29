package crazypants.enderio.base.fluid;

import javax.annotation.Nonnull;

import net.minecraftforge.fluids.Fluid;

public interface IFluidCoolant {

  @Nonnull
  Fluid getFluid();

  float getDegreesCoolingPerMB(float heat);
  
}
