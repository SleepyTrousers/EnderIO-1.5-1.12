package crazypants.enderio.base.power;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import crazypants.enderio.base.power.forge.PowerInterfaceForge;
import crazypants.enderio.base.power.forge.tile.ILegacyPoweredTile;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

public class PowerHandlerUtil {

  public static IPowerInterface getPowerInterface(@Nullable ICapabilityProvider provider, EnumFacing side) {
    if (provider != null) {
      @SuppressWarnings("null")
      final IEnergyStorage capability = provider.getCapability(CapabilityEnergy.ENERGY, side);
      if (capability != null) {
        return new PowerInterfaceForge(provider, capability);
      }
    }
    return null;
  }

  @SuppressWarnings("null")
  public static IEnergyStorage getCapability(@Nonnull ItemStack stack) {
    return stack.getCapability(CapabilityEnergy.ENERGY, null);
  }

  @SuppressWarnings("null")
  public static IEnergyStorage getCapability(@Nullable ICapabilityProvider provider, EnumFacing side) {
    return provider == null ? null : provider.getCapability(CapabilityEnergy.ENERGY, side);
  }

  public static int recieveInternal(@Nonnull ILegacyPoweredTile.Receiver target, int maxReceive, EnumFacing from, boolean simulate) {
    int result = Math.min(target.getMaxEnergyRecieved(from), maxReceive);
    result = Math.min(target.getMaxEnergyStored() - target.getEnergyStored(), result);
    result = Math.max(0, result);
    if (result > 0 && !simulate) {
      target.setEnergyStored(target.getEnergyStored() + result);
    }
    return result;
  }

}
