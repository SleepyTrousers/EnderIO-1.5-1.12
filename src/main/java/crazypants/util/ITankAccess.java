package crazypants.util;

import net.minecraftforge.fluids.FluidTank;

public interface ITankAccess {

  FluidTank getInputTank();

  FluidTank getOutputTank();

  void setTanksDirty();

}
