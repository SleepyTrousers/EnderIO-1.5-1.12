package crazypants.enderio.base.fluid;

import javax.annotation.Nonnull;

import net.minecraftforge.fluids.Fluid;

public interface IFluidCoolant {

  @Nonnull
  Fluid getFluid();

  /**
   * How much heat can one mB of the coolant absorb until it is evaporated completely?
   * 
   */
  default double getDegreesCoolingPerMB() {
    return (273.25 + 100.0 - getFluid().getTemperature()) * getDegreesCoolingPerMBPerK();
  }

  /**
   * How much heat can one mB of the coolant absorb until it heats up by 1 K?
   * 
   */
  double getDegreesCoolingPerMBPerK();

}
