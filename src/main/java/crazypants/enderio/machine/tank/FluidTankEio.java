package crazypants.enderio.machine.tank;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

public class FluidTankEio extends FluidTank {

  public FluidTankEio(int capacity) {
    super(capacity);
  }

  public float getFilledRatio() {
    return (float) getFluidAmount() / getCapacity();
  }

  public boolean isFull() {
    return getFluidAmount() >= getCapacity();
  }

  public boolean canDrainFluidType(FluidStack resource) {
    if(resource == null || resource.getFluid() == null || fluid == null) {
      return false;
    }
    return fluid.isFluidEqual(resource);
  }
  
  public boolean canDrainFluidType(Fluid fl) {
    if(fl == null || fluid == null) {
      return false;
    }
    return fl.getID() == fluid.fluidID;
  }

  public FluidStack drain(FluidStack resource, boolean doDrain) {
    if(!canDrainFluidType(resource)) {
      return null;
    }    
    return drain(resource.amount, doDrain);
  }

  

}
