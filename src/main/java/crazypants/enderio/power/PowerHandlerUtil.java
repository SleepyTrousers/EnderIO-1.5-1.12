package crazypants.enderio.power;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.energy.IEnergyStorage;

public class PowerHandlerUtil {

  private static final List<IPowerApiAdapter> providers = new CopyOnWriteArrayList<IPowerApiAdapter>();

  public static void addAdapter(@Nonnull IPowerApiAdapter adapter) {
    providers.add(adapter);
  }

  public static IPowerInterface getPowerInterface(@Nullable ICapabilityProvider provider, EnumFacing side) {
    IPowerInterface res = null;
    for (IPowerApiAdapter prov : providers) {
      res = prov.getPowerInterface(provider, side);
      if (res != null) {
        return res;
      }
    }
    return res;
  }

  public static IEnergyStorage getCapability(@Nonnull ItemStack stack) {
    return getCapability(stack, null);
  }

  public static IEnergyStorage getCapability(@Nullable ICapabilityProvider provider, EnumFacing side) {
    IEnergyStorage res = null;
    for (IPowerApiAdapter prov : providers) {
      res = prov.getCapability(provider, side);
      if (res != null) {
        return res;
      }
    }
    return res;
  }

  public static int recieveInternal(@Nonnull ILegacyPowerReceiver target, int maxReceive, EnumFacing from, boolean simulate) {
    int result = Math.min(target.getMaxEnergyRecieved(from), maxReceive);
    result = Math.min(target.getMaxEnergyStored() - target.getEnergyStored(), result);
    result = Math.max(0, result);
    if (result > 0 && !simulate) {
      target.setEnergyStored(target.getEnergyStored() + result);
    }
    return result;
  }

}
