package crazypants.enderio.conduit.liquid;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.liquids.ILiquidTank;
import net.minecraftforge.liquids.LiquidDictionary;
import net.minecraftforge.liquids.LiquidStack;

public class ConduitTank implements ILiquidTank {

  private LiquidStack fluid;
  private int capacity;
  private int tankPressure = 0;

  ConduitTank(int capacity) {
    this.capacity = capacity;
  }

  public int getFluidAmount() {
    return fluid == null ? 0 : fluid.amount;
  }

  public float getFilledRatio() {
    if(getFluidAmount() <= 0) {
      return 0;
    }
    if(getCapacity() <= 0) {
      return -1;
    }
    float res = (float) getFluidAmount() / getCapacity();
    return res;
  }

  public boolean isFull() {
    return getFluidAmount() >= getCapacity();
  }

  public void setAmount(int amount) {
    if(fluid != null) {
      fluid.amount = amount;
    }
  }

  public int getAvailableSpace() {
    return getCapacity() - getFluidAmount();
  }

  public void addAmount(int amount) {
    setAmount(getFluidAmount() + amount);
  }

  // @Override
  // public FluidTankInfo getInfo() {
  // return new FluidTankInfo(this);
  // }
  //

  @Override
  public LiquidStack getLiquid() {
    return this.fluid;
  }

  @Override
  public int getCapacity() {
    return this.capacity;
  }

  public void setLiquid(LiquidStack liquid) {
    this.fluid = liquid;
  }

  public void setCapacity(int capacity) {
    this.capacity = capacity;
    if(getFluidAmount() > capacity) {
      setAmount(capacity);
    }
  }

  @Override
  public int fill(LiquidStack resource, boolean doFill) {
    if(resource == null || resource.itemID <= 0) {
      return 0;
    }

    if(fluid == null || fluid.itemID <= 0) {
      if(resource.amount <= capacity) {
        if(doFill) {
          setLiquid(resource.copy());
        }
        return resource.amount;
      } else {
        if(doFill) {
          fluid = resource.copy();
          fluid.amount = capacity;
        }
        return capacity;
      }
    }

    if(!fluid.isLiquidEqual(resource)) {
      return 0;
    }

    int space = capacity - fluid.amount;
    if(resource.amount <= space) {
      if(doFill) {
        addAmount(resource.amount);
      }
      return resource.amount;
    } else {
      if(doFill) {
        fluid.amount = capacity;
      }
      return space;
    }

  }

  @Override
  public LiquidStack drain(int maxDrain, boolean doDrain) {
    if(fluid == null || fluid.itemID <= 0) {
      return null;
    }
    if(fluid.amount <= 0) {
      return null;
    }

    int used = maxDrain;
    if(fluid.amount < used) {
      used = fluid.amount;
    }

    if(doDrain) {
      addAmount(-used);
    }

    LiquidStack drained = new LiquidStack(fluid.itemID, used);

    if(fluid.amount < 0) {
      fluid.amount = 0;
    }
    return drained;
  }

  public void setTankPressure(int pressure) {
    this.tankPressure = pressure;
  }

  public String getLiquidName() {
    return fluid != null ? LiquidDictionary.findLiquidName(fluid) : null;
  }

  public boolean containsValidLiquid() {
    String name = LiquidDictionary.findLiquidName(fluid);
    return name != null && !name.isEmpty();
  }

  public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
    if(containsValidLiquid()) {
      fluid.writeToNBT(nbt);
    } else {
      nbt.setString("emptyTank", "");
    }
    return nbt;
  }

  public ILiquidTank readFromNBT(NBTTagCompound nbt) {
    if(!nbt.hasKey("emptyTank")) {
      LiquidStack liquid = LiquidStack.loadLiquidStackFromNBT(nbt);
      if(liquid != null) {
        setLiquid(liquid);
      }
    }
    return this;
  }

  @Override
  public int getTankPressure() {
    return 0;
  }

}
