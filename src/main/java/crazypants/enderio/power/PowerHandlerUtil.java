package crazypants.enderio.power;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import cofh.api.energy.IEnergyConnection;
import cofh.api.energy.IEnergyHandler;
import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;
import crazypants.enderio.machine.capbank.TileCapBank;

public class PowerHandlerUtil {

  public static final String STORED_ENERGY_NBT_KEY = "storedEnergyRF";

  public static IPowerInterface create(Object o) {
    if (o instanceof TileCapBank) {
      return new CapBankPI((TileCapBank) o);
    } else if (o instanceof IEnergyHandler || (o instanceof IEnergyProvider && o instanceof IEnergyReceiver)) {
      return new EnergyHandlerPI((IEnergyReceiver) o);
    } else if (o instanceof IEnergyProvider) {
      return new EnergyProviderPI((IEnergyProvider) o);
    } else if(o instanceof IEnergyReceiver) {
      return new EnergyReceiverPI((IEnergyReceiver) o);
    } else if(o instanceof IEnergyConnection) {
      return new EnergyConnectionPI((IEnergyConnection) o);
    }
    return null;
  }

  public static int getStoredEnergyForItem(ItemStack item) {
    NBTTagCompound tag = item.getTagCompound();
    if(tag == null) {
      return 0;
    }

    if(tag.hasKey("storedEnergy")) {
      double storedMj = tag.getDouble("storedEnergy");
      return (int) (storedMj * 10);
    }

    return tag.getInteger(STORED_ENERGY_NBT_KEY);
  }

  public static void setStoredEnergyForItem(ItemStack item, int storedEnergy) {
    NBTTagCompound tag = item.getTagCompound();
    if(tag == null) {
      tag = new NBTTagCompound();
    }
    tag.setInteger(STORED_ENERGY_NBT_KEY, storedEnergy);
    item.setTagCompound(tag);
  }


  public static int recieveInternal(IInternalPoweredTile target, int maxReceive, ForgeDirection from, boolean simulate) {
    int result = Math.min(target.getMaxEnergyRecieved(from), maxReceive);
    result = Math.min(target.getMaxEnergyStored() - target.getEnergyStored(), result);
    result = Math.max(0, result);
    if(result > 0 && !simulate) {
      target.setEnergyStored(target.getEnergyStored() + result);
    }
    return result;
  }

  
}
