package crazypants.enderio.power.forge;

import javax.annotation.Nullable;

import crazypants.enderio.EnderIO;
import crazypants.enderio.Log;
import crazypants.enderio.power.IInternalPowerReceiver;
import crazypants.enderio.power.IInternalPoweredItem;
import crazypants.enderio.power.IInternalPoweredTile;
import crazypants.enderio.power.IPowerApiAdapter;
import crazypants.enderio.power.IPowerInterface;
import crazypants.enderio.power.PowerHandlerUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.event.AttachCapabilitiesEvent.Item;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ForgeAdapter implements IPowerApiAdapter {

  @CapabilityInject(IEnergyStorage.class)
  public static final Capability<IEnergyStorage> ENERGY_HANDLER = null;
  
  private static final ResourceLocation KEY = new ResourceLocation(EnderIO.DOMAIN, "EioCapProviderPower");
  
  public static void capRegistered(Capability<?> cap) {
    PowerHandlerUtil.addAdapter(new ForgeAdapter());
    MinecraftForge.EVENT_BUS.register(ForgeAdapter.class);
    Log.info("Forge Energy integration loaded");
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
    if (provider != null && provider.hasCapability(ENERGY_HANDLER, side)) {
      return provider.getCapability(ENERGY_HANDLER, side);
    }
    return null;
  }

  @SubscribeEvent
  public static void attachCapabilities(net.minecraftforge.event.AttachCapabilitiesEvent.TileEntity evt) {
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
  public static void attachCapabilities(Item evt) {
    if(evt.getCapabilities().containsKey(KEY)) {
      return;
    }
    if(evt.getItem() instanceof IInternalPoweredItem) {
//      System.out.println("ForgeAdapter.attachCapabilities: Attached cap to " + evt.getItem());
      IInternalPoweredItem item = (IInternalPoweredItem)evt.getItem();
      evt.addCapability(KEY, new InternalPoweredItemWrapper.PoweredItemCapabilityProvider(item, evt.getItemStack()));
    }
  }
  
  
  
}