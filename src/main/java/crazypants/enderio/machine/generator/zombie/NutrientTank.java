package crazypants.enderio.machine.generator.zombie;

import crazypants.enderio.EnderIO;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

public class NutrientTank extends FluidTank {

  public NutrientTank(int capacity) {
    super(capacity);
  }

  @Override
  public int fill(FluidStack resource, boolean doFill) {
    if(resource == null || resource.getFluid().getID() != EnderIO.fluidNutrientDistillation.getID()) {
      return 0;
    }
    return super.fill(resource, doFill);
  }
  
  

}
