package crazypants.enderio.fluid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import cpw.mods.fml.common.Loader;
import crazypants.enderio.Log;

public class FluidFuelRegister implements IFluidRegister {

    public static final FluidFuelRegister instance = new FluidFuelRegister();

    private static final String KEY_FLUID_NAME = "fluidName";
    private static final String KEY_POWER_PER_CYCLE = "powerPerCycle";
    private static final String KEY_TOTAL_BURN_TIME = "totalBurnTime";
    private static final String KEY_COOLING_PER_MB = "coolingPerMb";

    private final Map<String, IFluidCoolant> coolants = new HashMap<String, IFluidCoolant>();
    private final Map<String, IFluidFuel> fuels = new HashMap<String, IFluidFuel>();

    private final List<IFluidRegister> otherRegisters = new ArrayList<IFluidRegister>();

    private FluidFuelRegister() {
        addCoolant(FluidRegistry.WATER, 0.0023f);
        if (Loader.isModLoaded("BuildCraft|Energy")) {
            try {
                IFluidRegister reg = (IFluidRegister) Class.forName("crazypants.enderio.fluid.BuildCraftFluidRegister")
                        .newInstance();
                otherRegisters.add(reg);
            } catch (Exception e) {
                Log.error("FluidFuelRegister: Error occured registering build craft fuels: " + e);
            }
        }
    }

    public void addCoolant(NBTTagCompound tag) {
        if (tag == null) {
            return;
        }
        if (!tag.hasKey(KEY_FLUID_NAME)) {
            return;
        }
        if (!tag.hasKey(KEY_COOLING_PER_MB)) {
            return;
        }
        addCoolant(tag.getString(KEY_FLUID_NAME), tag.getFloat(KEY_COOLING_PER_MB));
    }

    public void addCoolant(String fluidName, float degreesCoolingPerMB) {
        addCoolant(FluidRegistry.getFluid(fluidName), degreesCoolingPerMB);
    }

    @Override
    public void addCoolant(Fluid fluid, float degreesCoolingPerMB) {
        if (fluid == null || coolants.get(fluid.getName()) != null) {
            return;
        }
        coolants.put(fluid.getName(), new CoolantImpl(fluid, degreesCoolingPerMB));
        for (IFluidRegister reg : otherRegisters) {
            reg.addCoolant(fluid, degreesCoolingPerMB);
        }
    }

    @Override
    public IFluidCoolant getCoolant(Fluid fluid) {
        if (fluid == null) {
            return null;
        }
        IFluidCoolant res = coolants.get(fluid.getName());
        if (res == null && !coolants.containsKey(fluid.getName())) {
            for (IFluidRegister reg : otherRegisters) {
                res = reg.getCoolant(fluid);
                if (res != null) {
                    break;
                }
            }
            coolants.put(fluid.getName(), res);
        }
        return res;
    }

    @Override
    public IFluidCoolant getCoolant(FluidStack fluid) {
        if (fluid == null || fluid.getFluid() == null) {
            return null;
        }
        return getCoolant(fluid.getFluid());
    }

    public void addFuel(NBTTagCompound tag) {
        if (tag == null) {
            return;
        }
        if (!tag.hasKey(KEY_FLUID_NAME)) {
            return;
        }
        if (!tag.hasKey(KEY_POWER_PER_CYCLE)) {
            return;
        }
        if (!tag.hasKey(KEY_TOTAL_BURN_TIME)) {
            return;
        }
        addFuel(
                tag.getString(KEY_FLUID_NAME),
                tag.getInteger(KEY_POWER_PER_CYCLE),
                tag.getInteger(KEY_TOTAL_BURN_TIME));
    }

    public void addFuel(String fluidName, int powerPerCycleRF, int totalBurnTime) {
        addFuel(FluidRegistry.getFluid(fluidName), powerPerCycleRF, totalBurnTime);
    }

    @Override
    public void addFuel(Fluid fluid, int powerPerCycleRF, int totalBurnTime) {
        if (fluid == null || fuels.get(fluid.getName()) != null) {
            return;
        }
        fuels.put(fluid.getName(), new FuelImpl(fluid, powerPerCycleRF, totalBurnTime));
        for (IFluidRegister reg : otherRegisters) {
            reg.addFuel(fluid, powerPerCycleRF, totalBurnTime);
        }
    }

    @Override
    public IFluidFuel getFuel(Fluid fluid) {
        if (fluid == null) {
            return null;
        }
        IFluidFuel res = fuels.get(fluid.getName());
        if (res == null && !fuels.containsKey(fluid.getName())) {
            for (IFluidRegister reg : otherRegisters) {
                res = reg.getFuel(fluid);
                if (res != null) {
                    break;
                }
            }
            fuels.put(fluid.getName(), res);
        }
        return res;
    }

    @Override
    public IFluidFuel getFuel(FluidStack fluid) {
        if (fluid == null || fluid.getFluid() == null) {
            return null;
        }
        return getFuel(fluid.getFluid());
    }

    public static class FuelImpl implements IFluidFuel {

        private final Fluid fluid;
        private final int powerPerCycle;
        private final int totalBurningTime;

        public FuelImpl(Fluid fluid, int powerPerCycle, int totalBurningTime) {
            this.fluid = fluid;
            this.powerPerCycle = powerPerCycle;
            this.totalBurningTime = totalBurningTime;
        }

        @Override
        public Fluid getFluid() {
            return fluid;
        }

        @Override
        public int getTotalBurningTime() {
            return totalBurningTime;
        }

        @Override
        public int getPowerPerCycle() {
            return powerPerCycle;
        }
    }

    public static class CoolantImpl implements IFluidCoolant {

        private final Fluid fluid;
        private final float degreesCoolingPerMB;

        public CoolantImpl(Fluid fluid, float degreesCoolingPerMB) {
            this.fluid = fluid;
            this.degreesCoolingPerMB = degreesCoolingPerMB;
        }

        @Override
        public Fluid getFluid() {
            return fluid;
        }

        @Override
        public float getDegreesCoolingPerMB(float heat) {
            return degreesCoolingPerMB;
        }
    }
}
