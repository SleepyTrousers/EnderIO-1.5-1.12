package crazypants.enderio.tool;

import com.google.common.base.Strings;

import crazypants.enderio.EnderIO;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

public class SmartTank extends FluidTank {

  protected Fluid restriction;
  
  public SmartTank(FluidStack liquid, int capacity) {
    super(liquid, capacity);
    if (liquid != null) {
      restriction = liquid.getFluid();
    } else {
      restriction = null;
    }
  }

  public SmartTank(int capacity) {
    super(capacity);
  }

  public SmartTank(Fluid liquid, int capacity) {
    super(capacity);
    restriction = liquid;
  }

  public void setRestriction(Fluid restriction) {
    this.restriction = restriction;
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
    return fl.getID() == fluid.getFluidID();
  }

  public FluidStack drain(FluidStack resource, boolean doDrain) {
    if(!canDrainFluidType(resource)) {
      return null;
    }    
    return drain(resource.amount, doDrain);
  }

  public boolean canFill(FluidStack resource) {
    if (fluid != null) {
      return fluid.isFluidEqual(resource);
    } else if (restriction != null) {
      return resource.getFluid() != null && restriction.getID() == resource.getFluid().getID();
    } else {
      return true;
    }
  }

  public boolean canFill(Fluid fl) {
    if (fluid != null) {
      return fluid.getFluid().getID() == fl.getID();
    } else if (restriction != null) {
      return restriction.getID() == fl.getID();
    } else {
      return true;
    }
  }

  public void setFluidAmount(int amount) {
    if(amount > 0) {
      if (fluid != null) {
        fluid.amount =  Math.min(capacity, amount);
      } else if (restriction != null) {
        setFluid(new FluidStack(restriction, Math.min(capacity, amount)));
      } else {
        throw new RuntimeException("Cannot set fluid amount of an empty tank");
      }
    } else {
      setFluid(null);
    }
  }

  @Override
  public int fill(FluidStack resource, boolean doFill) {
    if(!canFill(resource)) {
      return 0;
    }
    return super.fill(resource, doFill);
  }

  @Override
  public FluidStack getFluid() {
    if (fluid != null) {
      return fluid;
    } else if (restriction != null) {
      return new FluidStack(restriction, 0);
    } else {
      return null;
    }
  }

  public int getAvailableSpace() {
    return getCapacity() - getFluidAmount();
  }

  public void addFluidAmount(int amount) {
    setFluidAmount(getFluidAmount() + amount);
  }

  @Override
  public void setCapacity(int capacity) {
    super.setCapacity(capacity);
    if(getFluidAmount() > capacity) {
      setFluidAmount(capacity);
    }
  }
  
  public void writeCommon(String name, NBTTagCompound nbtRoot) {
    if(getFluidAmount() > 0 || restriction != null) {
      NBTTagCompound tankRoot = new NBTTagCompound();
      writeToNBT(tankRoot);
      if (restriction != null) {
        tankRoot.setString("FluidRestriction", FluidRegistry.getFluidName(restriction.getID()));
      }
      nbtRoot.setTag(name, tankRoot);
    } else {
      nbtRoot.removeTag(name);
    }
  }

  public void readCommon(String name, NBTTagCompound nbtRoot) {
    NBTTagCompound tankRoot = (NBTTagCompound) nbtRoot.getTag(name);
    if(tankRoot != null) {
      readFromNBT(tankRoot);
      restriction = null;
      if (tankRoot.hasKey("FluidRestriction")) {
        String fluidName = tankRoot.getString("FluidRestriction");
        if (!Strings.isNullOrEmpty(fluidName)) {
          restriction = FluidRegistry.getFluid(fluidName);
        }
      }
    } else {
      setFluid(null);
      // not reseting 'restriction' here on purpose
    }
  }
}
