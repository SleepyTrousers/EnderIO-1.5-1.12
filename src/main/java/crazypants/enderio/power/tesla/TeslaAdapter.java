package crazypants.enderio.power.tesla;

import javax.annotation.Nullable;

import crazypants.enderio.EnderIO;
import crazypants.enderio.Log;
import crazypants.enderio.power.IInternalPowerReceiver;
import crazypants.enderio.power.IInternalPoweredItem;
import crazypants.enderio.power.IInternalPoweredTile;
import crazypants.enderio.power.IPowerApiAdapter;
import crazypants.enderio.power.IPowerInterface;
import crazypants.enderio.power.PowerHandlerUtil;
import crazypants.enderio.power.forge.PowerInterfaceForge;
import net.darkhax.tesla.api.ITeslaConsumer;
import net.darkhax.tesla.api.ITeslaHolder;
import net.darkhax.tesla.api.ITeslaProducer;
import net.darkhax.tesla.capability.TeslaCapabilities;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class TeslaAdapter implements IPowerApiAdapter {
  

  private static final ResourceLocation KEY = new ResourceLocation(EnderIO.DOMAIN, "EioCapProviderTesla");
  
  public static void create() throws Exception {
    // Make sure we can load these classes or throw an exception
    Class.forName("net.darkhax.tesla.capability.TeslaCapabilities");
    // class is now loaded and @CapabilityInjects are active
  }

  @CapabilityInject(ITeslaHolder.class)
  private static void capRegistered(Capability<ITeslaHolder> cap) {
    PowerHandlerUtil.addAdapter(new TeslaAdapter());
    MinecraftForge.EVENT_BUS.register(TeslaAdapter.class);
    Log.info("Tesla integration loaded");
  }

  @Override
  public IPowerInterface getPowerInterface(@Nullable ICapabilityProvider provider, EnumFacing side) {
    IEnergyStorage cap = getCapability(provider, side);
    if (cap != null) {
      return new PowerInterfaceForge(provider, cap);
    }
    return null;
  }

  @Override
  public IEnergyStorage getCapability(@Nullable ICapabilityProvider provider, EnumFacing side) {
    if(provider == null) {
      return null;
    }
    ITeslaHolder capHolder = null;
    if (provider.hasCapability(TeslaCapabilities.CAPABILITY_HOLDER, side)) {
      capHolder = provider.getCapability(TeslaCapabilities.CAPABILITY_HOLDER, side);
    }
    ITeslaConsumer capConsumer = null;
    if (provider.hasCapability(TeslaCapabilities.CAPABILITY_CONSUMER, side)) {
      capConsumer = provider.getCapability(TeslaCapabilities.CAPABILITY_CONSUMER, side);
    }
    ITeslaProducer capProducer = null;
    if (provider.hasCapability(TeslaCapabilities.CAPABILITY_PRODUCER, side)) {
      capProducer = provider.getCapability(TeslaCapabilities.CAPABILITY_PRODUCER, side);
    }
    if(capHolder == null && capConsumer == null && capProducer == null) {
      return null;
    }
    return new TeslaToForgeAdapter(capHolder, capConsumer, capProducer);
  }

  @SubscribeEvent
  public static void attachCapabilities(AttachCapabilitiesEvent.TileEntity evt) {
    if(evt.getCapabilities().containsKey(KEY)) {
      return;
    }
    TileEntity te = evt.getTileEntity();
    if (te instanceof IInternalPowerReceiver) {
      evt.addCapability(KEY, new InternalRecieverTileWrapper.RecieverTileCapabilityProvider((IInternalPowerReceiver) te));
    } else if (te instanceof IInternalPoweredTile) {
      evt.addCapability(KEY, new InternalPoweredTileWrapper.PoweredTileCapabilityProvider((IInternalPoweredTile) te));
    }
  }

  @SubscribeEvent
  public static void attachCapabilities(AttachCapabilitiesEvent.Item evt) {
    if(evt.getCapabilities().containsKey(KEY)) {
      return;
    }
    if(evt.getItem() instanceof IInternalPoweredItem) {
      IInternalPoweredItem item = (IInternalPoweredItem)evt.getItem();
      evt.addCapability(KEY, new InternalPoweredItemWrapper.PoweredItemCapabilityProvider(item, evt.getItemStack()));
    }
  }
  
}
