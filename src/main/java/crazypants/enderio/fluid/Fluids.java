package crazypants.enderio.fluid;

import crazypants.util.Lang;
import net.minecraftforge.fluids.IFluidTank;

public class Fluids {

  public static final String NUTRIENT_DISTILLATION_NAME = "nutrient_distillation";

  public static final String HOOTCH_NAME = "hootch";
  
  public static String toCapactityString(IFluidTank tank) {
    if(tank == null) {
      return "0/0 " + MB(); 
    }       
    return tank.getFluidAmount() + "/" + tank.getCapacity() + " " + MB();
  }
  
  public static String MB() {
    return Lang.localize("fluid.millibucket.abr");
  }
  

}
