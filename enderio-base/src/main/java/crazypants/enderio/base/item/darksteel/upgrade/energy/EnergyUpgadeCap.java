package crazypants.enderio.base.item.darksteel.upgrade.energy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

public class EnergyUpgadeCap implements IEnergyStorage, ICapabilityProvider {
  
  private final @Nonnull ItemStack container;

  public EnergyUpgadeCap(@Nonnull ItemStack container) {
    this.container = container;
  }

  @Override
  public int receiveEnergy(int maxReceive, boolean simulate) {
    return EnergyUpgradeManager.receiveEnergy(container, maxReceive, simulate);
  }

  @Override
  public int extractEnergy(int maxExtract, boolean simulate) {
    return EnergyUpgradeManager.extractEnergy(container, maxExtract, simulate);
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
    return true;
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
