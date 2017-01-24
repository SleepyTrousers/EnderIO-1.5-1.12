package crazypants.enderio.fluid;

import com.enderio.core.api.common.util.ITankAccess;
import com.enderio.core.common.util.FluidUtil;
import com.google.common.base.Strings;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidEvent;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

// TODO 1.11: REMOVE for ec version
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

  /**
   * Checks if the given fluid can actually be removed from this tank
   * <p>
   * Used by: te.canDrain()
   */
  public boolean canDrain(Fluid fl) {
    if (fluid == null || fl == null || !canDrain()) {
      return false;
    }

    return FluidUtil.areFluidsTheSame(fl, fluid.getFluid());
  }

  /**
   * Checks if the given fluid can actually be removed from this tank
   * <p>
   * Used by: internal
   */
  public boolean canDrain(FluidStack fluidStack) {
    if (fluid == null || fluidStack == null || !canDrain()) {
      return false;
    }    
    
    return fluidStack.isFluidEqual(fluid);
  }

  /**
   * Checks if the given fluid can actually be added to this tank (ignoring fill level)
   * <p>
   * Used by: internal
   */
  public boolean canFill(FluidStack resource) {
    if (!canFillFluidType(resource)) {
      return false;
    } else if (fluid != null) {
      return fluid.isFluidEqual(resource);
    } else {
      return true;
    }
  }

  /**
   * Checks if the given fluid can actually be added to this tank (ignoring fill level)
   * <p>
   * Used by: te.canFill()
   */
  public boolean canFill(Fluid fl) {
    if (fl == null || !canFillFluidType(new FluidStack(fl, 1))) {
      return false;
    } else if (fluid != null) {
      return FluidUtil.areFluidsTheSame(fluid.getFluid(), fl);
    } else {
      return true;
    }
  }

  @Override
  public boolean canFillFluidType(FluidStack resource) {
    return super.canFillFluidType(resource)
        && (restriction == null || (resource != null && resource.getFluid() != null && FluidUtil.areFluidsTheSame(restriction, resource.getFluid())));
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
    onContentsChanged();
  }

  @Override
  public int fill(FluidStack resource, boolean doFill) {
    if(!canFill(resource)) {
      return 0;
    }
    return fillInternal(resource, doFill);
  }

  @Override
  public FluidStack drain(FluidStack resource, boolean doDrain) {
    // TODO Auto-generated method stub
    return super.drain(resource, doDrain);
  }

  @Override
  public FluidStack drain(int maxDrain, boolean doDrain) {
    // TODO Auto-generated method stub
    return super.drain(maxDrain, doDrain);
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
    if (tile != null) {
      FluidEvent.fireEvent(new FluidEvent.FluidFillingEvent(fluid, tile.getWorld(), tile.getPos(), this, amount));
    }
  }

  public int removeFluidAmount(int amount) {
    int drained = 0;
    if (getFluidAmount() > amount) {
      setFluidAmount(getFluidAmount() - amount);
      drained = amount;
    } else if (!isEmpty()) {
      drained = getFluidAmount();
      setFluidAmount(0);
    } else {
      return 0;
    }
    if (tile != null) {
      FluidEvent.fireEvent(new FluidEvent.FluidDrainingEvent(fluid, tile.getWorld(), tile.getPos(), this, drained));
    }
    return drained;
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

  @Override
  protected void onContentsChanged() {
    super.onContentsChanged();
    if (tile instanceof ITankAccess) {
      ((ITankAccess) tile).setTanksDirty();
    } else if (tile != null) {
      tile.markDirty();
    }
  }

}
