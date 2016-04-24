package crazypants.enderio.tool;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

import com.enderio.core.common.util.FluidUtil;
import com.google.common.base.Strings;

public class SmartTank extends FluidTank {

  // Note: NBT-safe as long as the restriction isn't using NBT

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

  public boolean isEmpty() {
    return getFluidAmount() == 0;
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
    
    return FluidUtil.areFluidsTheSame(fl, fluid.getFluid());
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
      return resource.getFluid() != null && FluidUtil.areFluidsTheSame(restriction, resource.getFluid());
    } else {
      return true;
    }
  }

  public boolean canFill(Fluid fl) {
    if (fluid != null) {
      return FluidUtil.areFluidsTheSame(fluid.getFluid(), fl);
    } else if (restriction != null) {
      return FluidUtil.areFluidsTheSame(restriction, fl);
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
    NBTTagCompound tankRoot = new NBTTagCompound();
    writeToNBT(tankRoot);
    if (restriction != null) {
      tankRoot.setString("FluidRestriction", restriction.getName());
    }
    tankRoot.setInteger("Capacity", capacity);
    nbtRoot.setTag(name, tankRoot);
  }

  public void readCommon(String name, NBTTagCompound nbtRoot) {
    NBTTagCompound tankRoot = (NBTTagCompound) nbtRoot.getTag(name);
    if(tankRoot != null) {
      readFromNBT(tankRoot);
      if (tankRoot.hasKey("FluidRestriction")) {
        String fluidName = tankRoot.getString("FluidRestriction");
        if (!Strings.isNullOrEmpty(fluidName)) {
          restriction = FluidRegistry.getFluid(fluidName);
        }
      }
      if (tankRoot.hasKey("Capacity")) {
        capacity = tankRoot.getInteger("Capacity");
      }
    } else {
      setFluid(null);
      // not reseting 'restriction' here on purpose
    }
  }

  public static SmartTank createFromNBT(String name, NBTTagCompound nbtRoot) {
    SmartTank result = new SmartTank(0);
    result.readCommon(name, nbtRoot);
    if (result.getFluidAmount() > result.getCapacity()) {
      result.setCapacity(result.getFluidAmount());
    }
    return result;
  }

}
