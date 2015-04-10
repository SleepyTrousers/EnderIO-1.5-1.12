package crazypants.enderio.machine.generator.zombie;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import crazypants.enderio.init.EIOFluids;

public class NutrientTank extends FluidTank {

  public NutrientTank(int capacity) {
    super(capacity);
  }

  @Override
  public int fill(FluidStack resource, boolean doFill) {
    if(resource == null || resource.getFluid().getID() != EIOFluids.fluidNutrientDistillation.getID()) {
      return 0;
    }
    return super.fill(resource, doFill);
  }

  public float getFilledRatio() {
    return (float) getFluidAmount() / getCapacity();
  }
  
  

}
