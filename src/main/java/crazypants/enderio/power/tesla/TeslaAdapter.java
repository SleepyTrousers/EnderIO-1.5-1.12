package crazypants.enderio.power.tesla;

import javax.annotation.Nullable;

import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.EnderIO;
import crazypants.enderio.Log;
import crazypants.enderio.power.ILegacyPowerReceiver;
import crazypants.enderio.power.ILegacyPoweredTile;
import crazypants.enderio.power.IPowerApiAdapter;
import crazypants.enderio.power.IPowerInterface;
import crazypants.enderio.power.ItemPowerCapabilityBackend;
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
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class TeslaAdapter implements IPowerApiAdapter {

  private static final ResourceLocation KEY = new ResourceLocation(EnderIO.DOMAIN, "teslaadapter");

  public static void capRegistered(Capability<?> cap) {
    PowerHandlerUtil.addAdapter(new TeslaAdapter());
    MinecraftForge.EVENT_BUS.register(TeslaAdapter.class);
    ItemPowerCapabilityBackend.register(new InternalPoweredItemWrapper.PoweredItemCapabilityProvider());
    Log.info("Tesla integration loaded");
  }

  @Override
  public IPowerInterface getPowerInterface(@Nullable ICapabilityProvider provider, @Nullable EnumFacing side) {
    if (provider != null) {
      IEnergyStorage cap = getCapability(provider, side);
      if (cap != null) {
        return new PowerInterfaceForge(provider, cap);
      }
    }
    return null;
  }

  @Override
  public IEnergyStorage getCapability(@Nullable ICapabilityProvider provider, @Nullable EnumFacing side) {
    if (provider == null) {
      return null;
    }
    ITeslaHolder capHolder = provider.getCapability(NullHelper.notnull(TeslaCapabilities.CAPABILITY_HOLDER, "Tesla broke"), side);
    ITeslaConsumer capConsumer = provider.getCapability(NullHelper.notnull(TeslaCapabilities.CAPABILITY_CONSUMER, "Tesla broke"), side);
    ITeslaProducer capProducer = provider.getCapability(NullHelper.notnull(TeslaCapabilities.CAPABILITY_PRODUCER, "Tesla broke"), side);
    if (capHolder == null && capConsumer == null && capProducer == null) {
      return null;
    }
    return new TeslaToForgeAdapter(capHolder, capConsumer, capProducer);
  }

  @SubscribeEvent
  public static void attachCapabilities(AttachCapabilitiesEvent.TileEntity evt) {
    if (evt.getCapabilities().containsKey(KEY)) {
      return;
    }
    TileEntity te = evt.getTileEntity();
    if (te instanceof ILegacyPowerReceiver) {
      evt.addCapability(KEY, new InternalRecieverTileWrapper.RecieverTileCapabilityProvider((ILegacyPowerReceiver) te));
    } else if (te instanceof ILegacyPoweredTile) {
      evt.addCapability(KEY, new InternalPoweredTileWrapper.PoweredTileCapabilityProvider((ILegacyPoweredTile) te));
    }
  }

}
