package crazypants.enderio.fluid;

import net.minecraftforge.fluids.IFluidTank;

import crazypants.enderio.EnderIO;

public class Fluids {

    public static final String NUTRIENT_DISTILLATION = "nutrient_distillation";
    public static final String HOOTCH = "hootch";
    public static final String ROCKET_FUEL = "rocket_fuel";
    public static final String FIRE_WATER = "fire_water";
    public static final String LIQUID_SUNSHINE = "liquid_sunshine";
    public static final String CLOUD_SEED = "cloud_seed";
    public static final String CLOUD_SEED_CONCENTRATED = "cloud_seed_concentrated";
    public static final String ENDER_DISTILLATION = "ender_distillation";
    public static final String VAPOR_OF_LEVITY = "vapor_of_levity";

    public static String toCapactityString(IFluidTank tank) {
        if (tank == null) {
            return "0/0 " + MB();
        }
        return tank.getFluidAmount() + "/" + tank.getCapacity() + " " + MB();
    }

    public static String MB() {
        return EnderIO.lang.localize("fluid.millibucket.abr");
    }
}
