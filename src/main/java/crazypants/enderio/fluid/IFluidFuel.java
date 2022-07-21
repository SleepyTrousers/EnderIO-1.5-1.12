package crazypants.enderio.fluid;

import net.minecraftforge.fluids.Fluid;

public interface IFluidFuel {

    Fluid getFluid();

    int getTotalBurningTime();

    int getPowerPerCycle();
}
