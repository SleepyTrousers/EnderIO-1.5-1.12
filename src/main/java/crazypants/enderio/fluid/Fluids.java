package crazypants.enderio.fluid;

import crazypants.enderio.EnderIO;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.IFluidTank;

public class Fluids {

  public static final String NUTRIENT_DISTILLATION_NAME = "nutrient_distillation";

  public static final String HOOTCH_NAME = "hootch";

  public static final String ROCKET_FUEL_NAME = "rocket_fuel";

  public static final String FIRE_WATER_NAME = "fire_water";

  public static ResourceLocation getStill(String fluidName) {
    return new ResourceLocation(EnderIO.MODID, fluidName + "_still");
  }
  
  public static ResourceLocation getFlowing(String fluidName) {
    return new ResourceLocation(EnderIO.MODID, fluidName + "_flow");
  }
  
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
