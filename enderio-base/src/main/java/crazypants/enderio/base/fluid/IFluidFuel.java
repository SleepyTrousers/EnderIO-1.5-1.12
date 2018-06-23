package crazypants.enderio.base.fluid;

import javax.annotation.Nonnull;

import net.minecraftforge.fluids.Fluid;

public interface IFluidFuel {

  @Nonnull
  Fluid getFluid();

  /**
   * Total burn time of one bucket of fuel
   */
  int getTotalBurningTime();

  /**
   * Amount of energy created per tick in a base-line machine
   */
  int getPowerPerCycle();
}
