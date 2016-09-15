package crazypants.enderio.power;

import javax.annotation.Nullable;

import cofh.api.energy.IEnergyConnection;
import cofh.api.energy.IEnergyContainerItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.energy.IEnergyStorage;

public class PowerHandlerUtil {

//  public static final @Nonnull String STORED_ENERGY_NBT_KEY = "storedEnergyRF";

  @CapabilityInject(IEnergyStorage.class)
  private static final Capability<IEnergyStorage> ENERGY_HANDLER = null;

  public static IPowerInterface create(@Nullable ICapabilityProvider provider, EnumFacing side) {
    if (provider instanceof IInternalPoweredTile) {
      return new PowerInterfaceInternal((IInternalPoweredTile) provider, side);
    }
    IEnergyStorage cap = getCapability(provider, side);
    if (cap != null) {
      return new PowerInterfaceForge(cap);
    }
    if (provider instanceof IEnergyConnection) {
     return new PowerInterfaceRF((IEnergyConnection) provider, side);
    }
    return null;
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
    result = Math.min(target.getMaxEnergyStored() - target.getEnergyStored(), result);
    result = Math.max(0, result);
    if (result > 0 && !simulate) {
      target.setEnergyStored(target.getEnergyStored() + result);
    }
    return result;
  }

}
