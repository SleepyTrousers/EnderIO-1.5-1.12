package crazypants.enderio.fluid;

import buildcraft.api.fuels.BuildcraftFuelRegistry;
import buildcraft.api.fuels.ICoolant;
import buildcraft.api.fuels.IFuel;
import crazypants.enderio.fluid.FluidFuelRegister.CoolantImpl;
import crazypants.enderio.fluid.FluidFuelRegister.FuelImpl;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

public class BuildCraftFluidRegister implements IFluidRegister {

    public BuildCraftFluidRegister() throws Exception {
        // Make it go splat in object construction if an older version of
        // build craft is installed
        Class.forName("buildcraft.api.fuels.BuildcraftFuelRegistry");
    }

    @Override
    public void addCoolant(Fluid fluid, float degreesCoolingPerMB) {
        if (BuildcraftFuelRegistry.coolant != null && BuildcraftFuelRegistry.coolant.getCoolant(fluid) == null) {
            BuildcraftFuelRegistry.coolant.addCoolant(fluid, degreesCoolingPerMB);
        }
    }

    @Override
    public IFluidCoolant getCoolant(Fluid fluid) {
        if (fluid != null && BuildcraftFuelRegistry.coolant != null) {
            ICoolant bcCool = BuildcraftFuelRegistry.coolant.getCoolant(fluid);
            if (bcCool != null) {
                return new CoolantBC(bcCool);
            }
        }
        return null;
    }

    @Override
    public IFluidCoolant getCoolant(FluidStack fluid) {
        if (fluid == null || fluid.getFluid() == null) {
            return null;
        }
        return getCoolant(fluid.getFluid());
    }

    @Override
    public void addFuel(Fluid fluid, int powerPerCycleRF, int totalBurnTime) {
        if (BuildcraftFuelRegistry.fuel != null && BuildcraftFuelRegistry.fuel.getFuel(fluid) == null) {
            BuildcraftFuelRegistry.fuel.addFuel(fluid, powerPerCycleRF, totalBurnTime);
        }
    }

    @Override
    public IFluidFuel getFuel(Fluid fluid) {
        if (fluid == null) {
            return null;
        }
        if (BuildcraftFuelRegistry.fuel != null) {
            IFuel bcFuel = BuildcraftFuelRegistry.fuel.getFuel(fluid);
            if (bcFuel != null) {
                return new FuelBC(bcFuel);
            }
        }
        return null;
    }

    @Override
    public IFluidFuel getFuel(FluidStack fluid) {
        if (fluid == null || fluid.getFluid() == null) {
            return null;
        }
        return getFuel(fluid.getFluid());
    }

    private static class FuelBC extends FuelImpl {

        FuelBC(IFuel fuel) {
            super(fuel.getFluid(), fuel.getPowerPerCycle(), fuel.getTotalBurningTime());
        }
    }

    private static class CoolantBC extends CoolantImpl {

        CoolantBC(ICoolant coolant) {
            // NB: in the current BC impl the temperature to getDegreesCoolingPerMB is ignored
            super(coolant.getFluid(), coolant.getDegreesCoolingPerMB(100));
        }
    }
}
