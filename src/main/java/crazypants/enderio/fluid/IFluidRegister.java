package crazypants.enderio.fluid;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

public interface IFluidRegister {

    IFluidFuel getFuel(FluidStack fluid);

    IFluidFuel getFuel(Fluid fluid);

    void addFuel(Fluid fluid, int powerPerCycleRF, int totalBurnTime);

    IFluidCoolant getCoolant(FluidStack fluid);

    IFluidCoolant getCoolant(Fluid fluid);

    void addCoolant(Fluid fluid, float degreesCoolingPerMB);
}
