package crazypants.enderio.fluid;

import net.minecraftforge.fluids.IFluidTank;
import crazypants.enderio.EnderIO;

public class Fluids {

  public static final String NUTRIENT_DISTILLATION_NAME = "nutrient_distillation";

  public static final String HOOTCH_NAME = "hootch";

  public static final String ROCKET_FUEL_NAME = "rocket_fuel";

  public static final String FIRE_WATER_NAME = "fire_water";
  
  public static final String LIQUID_SUNSHINE_NAME = "liquid_sunshine";
  public static final String CLOUD_SEED_NAME = "cloud_seed";
  public static final String CLOUD_SEED_CONCENTRATED_NAME = "cloud_seed_concentrated";

  public static String toCapactityString(IFluidTank tank) {
    if(tank == null) {
      return "0/0 " + MB();
    }
    return tank.getFluidAmount() + "/" + tank.getCapacity() + " " + MB();
  }

  public static String MB() {
    return EnderIO.lang.localize("fluid.millibucket.abr");
  }


}
