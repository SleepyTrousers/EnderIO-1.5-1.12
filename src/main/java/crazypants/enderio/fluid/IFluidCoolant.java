package crazypants.enderio.fluid;

import net.minecraftforge.fluids.Fluid;

public interface IFluidCoolant {

  Fluid getFluid();

  float getDegreesCoolingPerMB(float heat);
  
}
