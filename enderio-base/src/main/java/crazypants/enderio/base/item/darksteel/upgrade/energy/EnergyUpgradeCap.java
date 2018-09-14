package crazypants.enderio.base.item.darksteel.upgrade.energy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import crazypants.enderio.api.upgrades.IDarkSteelItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

public class EnergyUpgradeCap implements IEnergyStorage, ICapabilityProvider {

  private final @Nonnull ItemStack container;

  public EnergyUpgradeCap(@Nonnull ItemStack container) {
    this.container = container;
  }

  @Override
  public int receiveEnergy(int maxReceive, boolean simulate) {
    return canReceive() ? EnergyUpgradeManager.receiveEnergy(container, maxReceive, simulate) : 0;
  }

  @Override
  public int extractEnergy(int maxExtract, boolean simulate) {
    return canExtract() ? EnergyUpgradeManager.extractEnergy(container, maxExtract, simulate) : 0;
  }

  @Override
  public int getEnergyStored() {
    return EnergyUpgradeManager.getEnergyStored(container);
  }

  @Override
  public int getMaxEnergyStored() {
    return EnergyUpgradeManager.getMaxEnergyStored(container);
  }

  @Override
  public boolean canExtract() {
    return ((IDarkSteelItem) container.getItem()).allowExtractEnergy();
  }

  @Override
  public boolean canReceive() {
    return true;
  }

  @Override
  public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
    return capability == CapabilityEnergy.ENERGY;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
    return capability == CapabilityEnergy.ENERGY ? (T) this : null;
  }

}
