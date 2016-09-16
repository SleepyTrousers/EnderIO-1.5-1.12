package crazypants.enderio.power;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import crazypants.enderio.EnderIO;
import crazypants.enderio.Log;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class PowerHandlerUtil {

  @CapabilityInject(IEnergyStorage.class)
  private static final Capability<IEnergyStorage> ENERGY_HANDLER = null;
  
  private static final List<IPowerApiAdapter> providers = new ArrayList<IPowerApiAdapter>();

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

  public static void postInit(FMLPostInitializationEvent event) {
    MinecraftForge.EVENT_BUS.register(new CapAttacher());
    providers.add(new ForgePowerProvider());
    try {
      Object o = Class.forName("crazypants.enderio.power.rf.RfAdpater").newInstance();
      if(o instanceof IPowerApiAdapter) {
        providers.add((IPowerApiAdapter)o);
        Log.info("RF integration loaded");
      }
    } catch(Exception e) {
      Log.info("RF API not found. RF integration not loaded.");
    }
    
  }

  public static class CapAttacher {

    @SubscribeEvent
    public void attachCapability(AttachCapabilitiesEvent.TileEntity evt) {
      TileEntity te = evt.getTileEntity();
      if (te instanceof IInternalPowerReceiver) {
        evt.addCapability(new ResourceLocation(EnderIO.DOMAIN, "EioCapProviderPower"), new PowerHandlerRecieverTile.RecieverTileCapabilityProvider((IInternalPowerReceiver) te));
      } else if (te instanceof IInternalPoweredTile) {
        evt.addCapability(new ResourceLocation(EnderIO.DOMAIN, "EioCapProviderPower"), new PowerHandlerPoweredTile.PoweredTileCapabilityProvider((IInternalPoweredTile) te));
      }
    }

  }
  
  private static class ForgePowerProvider implements IPowerApiAdapter {

    @Override
    public IPowerInterface getPowerInterface(ICapabilityProvider provider, EnumFacing side) {
      IEnergyStorage cap = getCapability(provider, side);
      if (cap != null) {
        return new PowerInterfaceForge(provider, cap);
      }
      return null;
    }

    @Override
    public IEnergyStorage getCapability(ICapabilityProvider provider, EnumFacing side) {
      if (provider != null && provider.hasCapability(ENERGY_HANDLER, side)) {
        return provider.getCapability(ENERGY_HANDLER, side);
      }
      return null;
    }
    
  }

}
