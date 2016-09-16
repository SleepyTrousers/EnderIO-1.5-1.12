package crazypants.enderio.power;

import javax.annotation.Nullable;

import cofh.api.energy.IEnergyConnection;
import cofh.api.energy.IEnergyContainerItem;
import crazypants.enderio.EnderIO;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class PowerHandlerUtil {

  @CapabilityInject(IEnergyStorage.class)
  private static final Capability<IEnergyStorage> ENERGY_HANDLER = null;

  public static IPowerInterface getPowerInterface(@Nullable ICapabilityProvider provider, EnumFacing side) {
    IEnergyStorage cap = getCapability(provider, side);
    if (cap != null) {
      return new PowerInterfaceForge(provider, cap);
    }
    if (provider instanceof IEnergyConnection) {
      return new PowerInterfaceRF((IEnergyConnection) provider, side);
    }
    return null;
  }

  public static IEnergyStorage getCapability(ItemStack stack) {
    return getCapability(stack, null);
  }

  public static IEnergyStorage getCapability(@Nullable ICapabilityProvider provider, EnumFacing side) {
    if (provider != null && provider.hasCapability(ENERGY_HANDLER, side)) {
      return provider.getCapability(ENERGY_HANDLER, side);
    } else if (provider instanceof ItemStack && ((ItemStack) provider).getItem() instanceof IEnergyContainerItem) {
      return new ItemWrapperRF((IEnergyContainerItem) ((ItemStack) provider).getItem(), (ItemStack) provider);
    }
    return null;
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
  }

  public static class CapAttacher {

    @SubscribeEvent
    public void attachCapability(AttachCapabilitiesEvent.TileEntity evt) {
      TileEntity te = evt.getTileEntity();
      if (te instanceof IInternalPowerReceiver) {
        evt.addCapability(new ResourceLocation(EnderIO.DOMAIN, "EioCapProviderPower"), new RecieverTileProvider((IInternalPowerReceiver) te));
      } else if (te instanceof IInternalPoweredTile) {
        evt.addCapability(new ResourceLocation(EnderIO.DOMAIN, "EioCapProviderPower"), new PoweredTileProvider((IInternalPoweredTile) te));
      }
    }

  }

  public static class PoweredTileProvider implements ICapabilityProvider {

    private final IInternalPoweredTile tile;

    public PoweredTileProvider(IInternalPoweredTile tile) {
      this.tile = tile;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
      return capability == CapabilityEnergy.ENERGY;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
      if (capability == CapabilityEnergy.ENERGY) {
        return (T) new PowerHandlerPoweredTile(tile, facing);
      }
      return null;
    }

  }

  public static class RecieverTileProvider extends PoweredTileProvider {

    private final IInternalPowerReceiver tile;

    public RecieverTileProvider(IInternalPowerReceiver tile) {
      super(tile);
      this.tile = tile;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
      if (capability == CapabilityEnergy.ENERGY) {
        return (T) new PowerHandlerRecieverTile(tile, facing);
      }
      return null;
    }

  }

}
