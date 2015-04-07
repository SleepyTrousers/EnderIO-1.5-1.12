package crazypants.util;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

public interface ITankAccess {

  /**
   * Find tank to insert fluid.
   * 
   * @param forFluidType
   *          The type of fluid that should be inserted. May be null.
   * @return An internal tank that can take the given type of fluid. If multiple
   *         tanks can take the fluid, the first one that is not full will be
   *         returned. If no tank can take the fluid, returns null.
   */
  FluidTank getInputTank(FluidStack forFluidType);

  /**
   * Get tank(s) to remove liquid from.
   * 
   * @return Tank that can be drained. Tanks are returned in order or priority.
   *         If there's no tank, an empty array is returned.
   */
  FluidTank[] getOutputTanks();

  /**
   * Will be called after a tank that was returned by one of the other methods
   * was manipulated.
   */
  void setTanksDirty();

}
