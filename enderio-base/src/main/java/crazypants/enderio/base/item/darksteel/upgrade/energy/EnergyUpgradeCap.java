package crazypants.enderio.base.item.darksteel.upgrade.energy;

import javax.annotation.Nonnull;

import crazypants.enderio.api.upgrades.IDarkSteelItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.energy.IEnergyStorage;

public class EnergyUpgradeCap implements IEnergyStorage {

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

}
