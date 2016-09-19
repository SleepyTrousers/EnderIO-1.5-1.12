package crazypants.enderio.power;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.annotation.Nullable;

import crazypants.enderio.Log;
import crazypants.enderio.power.forge.ForgeAdapter;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class PowerHandlerUtil {

  private static final List<IPowerApiAdapter> providers = new CopyOnWriteArrayList<IPowerApiAdapter>();
  
  public static void addAdapter(IPowerApiAdapter adapter) {
    if(adapter != null) {
      providers.add(adapter);
    }
  }
  
  public static void onPostInit(FMLPostInitializationEvent event) {
    providers.add(new ForgeAdapter());
    try {
      IPowerApiAdapter o = (IPowerApiAdapter)Class.forName("crazypants.enderio.power.rf.RfAdpater").newInstance();
      providers.add(o);
      Log.info("RF integration loaded");
    } catch(Exception e) {
      Log.warn("RF API not found. RF integration not loaded.");
    }
    try {
      IPowerApiAdapter o = (IPowerApiAdapter)Class.forName("crazypants.enderio.power.tesla.TeslaAdapter").newInstance();
      providers.add(o);
      Log.info("Tesla integration loaded");
    } catch(Exception e) {
      Log.warn("Tesla API not found. Tesla integration not loaded.");
    }
    MinecraftForge.EVENT_BUS.register(new CapAttacher());
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

  public static class CapAttacher {

    @SubscribeEvent
    public void attachCapabilities(AttachCapabilitiesEvent.TileEntity evt) {
      for(IPowerApiAdapter prov : providers) {
        prov.attachCapabilities(evt);
      }
    }
    
    @SubscribeEvent
    public void attachCapabilities(AttachCapabilitiesEvent.Item evt) {
      for(IPowerApiAdapter prov : providers) {
        prov.attachCapabilities(evt);
      }
    }

  }

}
