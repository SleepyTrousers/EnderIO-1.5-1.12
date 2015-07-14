package crazypants.enderio.conduit.gas;

import mekanism.api.gas.GasStack;
import mekanism.api.gas.GasTank;
import net.minecraft.nbt.NBTTagCompound;
import cpw.mods.fml.common.Optional.Method;

public class ConduitGasTank extends GasTank {

  private int capacity;

  ConduitGasTank(int capacity) {
    super(capacity);
    this.capacity = capacity;
  }

  public float getFilledRatio() {
    if(getStored() <= 0) {
      return 0;
    }
    if(getMaxGas() <= 0) {
      return -1;
    }
    float res = (float) getStored() / getMaxGas();
    return res;
  }

  public boolean isFull() {
    return getStored() >= getMaxGas();
  }

  public void setAmount(int amount) {
    if(stored != null) {
      stored.amount = amount;
    }
  }

  public int getAvailableSpace() {
    return getMaxGas() - getStored();
  }

  public void addAmount(int amount) {
    setAmount(getStored() + amount);
  }

  @Override
  public int getMaxGas() {
    return this.capacity;
  }

  public void setCapacity(int capacity) {
    this.capacity = capacity;
    if(getStored() > capacity) {
      setAmount(capacity);
    }
  }

  @Override
  @Method(modid = GasUtil.API_NAME)
  public int receive(GasStack resource, boolean doReceive) {
    if(resource == null || resource.getGas().getID() < 0) {
      return 0;
    }

    if(stored == null || stored.getGas().getID() < 0) {
      if(resource.amount <= capacity) {
        if(doReceive) {
          setGas(resource.copy());
        }
        return resource.amount;
      } else {
        if(doReceive) {
          stored = resource.copy();
          stored.amount = capacity;
        }
        return capacity;
      }
    }

    if(!stored.isGasEqual(resource)) {
      return 0;
    }

    int space = capacity - stored.amount;
    if(resource.amount <= space) {
      if(doReceive) {
        addAmount(resource.amount);
      }
      return resource.amount;
    } else {
      if(doReceive) {
        stored.amount = capacity;
      }
      return space;
    }

  }

  @Override
  @Method(modid = GasUtil.API_NAME)
  public GasStack draw(int maxDrain, boolean doDraw) {
    if(stored == null || stored.getGas().getID() < 0) {
      return null;
    }
    if(stored.amount <= 0) {
      return null;
    }

    int used = maxDrain;
    if(stored.amount < used) {
      used = stored.amount;
    }

    if(doDraw) {
      addAmount(-used);
    }

    GasStack drained = new GasStack(stored.getGas().getID(), used);

    if(stored.amount < 0) {
      stored.amount = 0;
    }
    return drained;
  }

  public String getGasName() {
    return stored != null ? stored.getGas().getLocalizedName() : null;
  }

  public boolean containsValidGas() {
    return GasUtil.isGasValid(stored);
  }

  public NBTTagCompound write(NBTTagCompound nbt) {
    if(containsValidGas()) {
      stored.write(nbt);
    } else {
      nbt.setBoolean("emptyGasTank", true);
    }
    return nbt;
  }

  public void read(NBTTagCompound nbt) {
    if(!nbt.hasKey("emptyGasTank")) {
      GasStack gas = GasStack.readFromNBT(nbt);
      if(gas != null) {
        setGas(gas);
      }
    }
  }

  public boolean isEmpty() {
    return stored == null || stored.amount == 0;
  }

}
