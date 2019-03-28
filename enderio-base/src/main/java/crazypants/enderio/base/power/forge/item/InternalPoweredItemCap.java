package crazypants.enderio.base.power.forge.item;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraftforge.energy.IEnergyStorage;

public class InternalPoweredItemCap implements IEnergyStorage {

  protected final @Nonnull ItemStack container;
  protected @Nonnull IInternalPoweredItem item;

  public InternalPoweredItemCap(@Nonnull IInternalPoweredItem item, @Nonnull ItemStack container) {
    this.container = container;
    this.item = item;
  }

  @Override
  public int receiveEnergy(int maxReceive, boolean simulate) {
    if (!canReceive()) {
      return 0;
    }
    int energy = getEnergyStored();
    int energyReceived = Math.min(getMaxEnergyStored() - energy, Math.min(item.getMaxInput(container), maxReceive));
    if (!simulate) {
      energy += energyReceived;
      setEnergyStored(energy);
    }
    return energyReceived;
  }

  @Override
  public int extractEnergy(int maxExtract, boolean simulate) {
    if (!canExtract()) {
      return 0;
    }
    int energy = getEnergyStored();
    int energyExtracted = Math.min(energy, Math.min(item.getMaxOutput(container), maxExtract));
    if (!simulate) {
      energy -= energyExtracted;
      setEnergyStored(energy);
    }
    return energyExtracted;
  }

  public void setEnergyStored(int energy) {
    item.setEnergyStored(container, energy);
  }

  @Override
  public int getEnergyStored() {
    return item.getEnergyStored(container);
  }

  @Override
  public int getMaxEnergyStored() {
    return item.getMaxEnergyStored(container);
  }

  @Override
  public boolean canExtract() {
    if (container.getCount() > 1) {
      return false;
    }
    return item.getMaxOutput(container) > 0;
  }

  @Override
  public boolean canReceive() {
    if (container.getCount() > 1) {
      return false;
    }
    return item.getMaxInput(container) > 0;
  }

}
