package crazypants.enderio.power.forge;

import crazypants.enderio.EnderIO;
import crazypants.enderio.power.IInternalPowerReceiver;
import crazypants.enderio.power.IInternalPoweredTile;
import crazypants.enderio.power.IPowerApiAdapter;
import crazypants.enderio.power.IPowerInterface;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.energy.IEnergyStorage;

public class ForgeAdapter implements IPowerApiAdapter {

  @CapabilityInject(IEnergyStorage.class)
  public static final Capability<IEnergyStorage> ENERGY_HANDLER = null;
  
  private static final ResourceLocation KEY = new ResourceLocation(EnderIO.DOMAIN, "EioCapProviderPower");
  
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

  @Override
  public void attachCapabilities(net.minecraftforge.event.AttachCapabilitiesEvent.TileEntity evt) {
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
  
}