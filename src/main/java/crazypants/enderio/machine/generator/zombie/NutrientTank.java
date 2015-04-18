package crazypants.enderio.machine.generator.zombie;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import crazypants.enderio.EnderIO;

public class NutrientTank extends FluidTank {

  public NutrientTank(int capacity) {
    super(capacity);
  }

  public boolean canFill(Fluid fluid) {
    return fluid != null && fluid.getID() == EnderIO.fluidNutrientDistillation.getID();
  }

  @Override
  public int fill(FluidStack resource, boolean doFill) {
    if(resource == null || !canFill(resource.getFluid())) {
      return 0;
    }
    return super.fill(resource, doFill);
  }

  public float getFilledRatio() {
    return (float) getFluidAmount() / getCapacity();
  }

  public void setFluidAmount(int amount) {
    if(amount > 0) {
      setFluid(new FluidStack(EnderIO.fluidNutrientDistillation, Math.min(capacity, amount)));
    } else {
      setFluid(null);
    }
  }

  public void writeCommon(String name, NBTTagCompound nbtRoot) {
    if(getFluidAmount() > 0) {
      NBTTagCompound tankRoot = new NBTTagCompound();
      writeToNBT(tankRoot);
      nbtRoot.setTag(name, tankRoot);
    }
  }

  public void readCommon(String name, NBTTagCompound nbtRoot) {
    NBTTagCompound tankRoot = (NBTTagCompound) nbtRoot.getTag(name);
    if(tankRoot != null) {
      readFromNBT(tankRoot);
    } else {
      setFluid(null);
    }
  }
}
