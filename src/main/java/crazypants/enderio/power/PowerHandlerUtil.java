package crazypants.enderio.power;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.annotation.Nullable;

import crazypants.enderio.Log;
import crazypants.enderio.power.forge.ForgeAdapter;
import crazypants.enderio.power.rf.RfAdapter;
import crazypants.enderio.power.tesla.TeslaAdapter;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.energy.IEnergyStorage;

public class PowerHandlerUtil {

  private static final List<IPowerApiAdapter> providers = new CopyOnWriteArrayList<IPowerApiAdapter>();
  
  public static void addAdapter(IPowerApiAdapter adapter) {
    if(adapter != null) {
      providers.add(adapter);
    }
  }
  
  /**
   * Prime power adapters. To be loaded very early, best before init phase.
   */
  public static void create() {
    try {
      ForgeAdapter.create();
    } catch (Throwable e) {
      Log.error("Forge not found. Forge Energy integration NOT loaded: " + e);
      if (Log.LOGGER.isDebugEnabled()) {
        e.printStackTrace();
      }
    }
    try {
      TeslaAdapter.create();
    } catch (Throwable e) {
      Log.warn("Tesla API not found. Tesla integration not loaded. This is not an error. Reason: " + e);
      if (Log.LOGGER.isDebugEnabled()) {
        e.printStackTrace();
      }
    }
    try {
      RfAdapter.create();
    } catch (Throwable e) {
      Log.warn("RF API not found. RF integration not loaded. This is not an error. Reason: " + e);
      if (Log.LOGGER.isDebugEnabled()) {
        e.printStackTrace();
      }
    }
  }
  
  public static IPowerInterface getPowerInterface(@Nullable ICapabilityProvider provider, EnumFacing side) {
    IPowerInterface res = null;
    for(IPowerApiAdapter prov : providers) {
      res = prov.getPowerInterface(provider, side);
      if(res != null) {
        return res;
      }
    }
    return res;
  }

  public static IEnergyStorage getCapability(ItemStack stack) {
    return getCapability(stack, null);
  }

  public static IEnergyStorage getCapability(@Nullable ICapabilityProvider provider, EnumFacing side) {
    IEnergyStorage res = null;
    for(IPowerApiAdapter prov : providers) {
      res = prov.getCapability(provider, side);
      if(res != null) {
        return res;
      }
    }
    return res;
  }

  public static int recieveInternal(IInternalPowerReceiver target, int maxReceive, EnumFacing from, boolean simulate) {
    int result = Math.min(target.getMaxEnergyRecieved(from), maxReceive);
    result = Math.min(target.getMaxEnergyStored(from) - target.getEnergyStored(from), result);
    result = Math.max(0, result);
    if (result > 0 && !simulate) {
      target.setEnergyStored(target.getEnergyStored(from) + result);
    }
    return result;
  }

}
