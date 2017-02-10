package crazypants.enderio.power.forge;

import crazypants.enderio.power.IInternalPoweredItem;
import crazypants.enderio.power.ItemPowerCapabilityProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

public class InternalPoweredItemWrapper implements IEnergyStorage {

  
  public static class PoweredItemCapabilityProvider implements ItemPowerCapabilityProvider {

    @Override
    public boolean hasCapability(ItemStack stack, Capability<?> capability, EnumFacing facing) {
      return capability == CapabilityEnergy.ENERGY;
    }

    @Override
    public <T> T getCapability(ItemStack stack, Capability<T> capability, EnumFacing facing) {
      if (capability == CapabilityEnergy.ENERGY) {
        return CapabilityEnergy.ENERGY.cast(new InternalPoweredItemWrapper(stack));
      }
      return null;
    }

  }
  
  protected final ItemStack container;
  protected IInternalPoweredItem item;

  public InternalPoweredItemWrapper(ItemStack container) {
    this.container = container;
    this.item = (IInternalPoweredItem) container.getItem();
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
    if(container.stackSize > 1) {
      return false;
    }
    return item.getMaxOutput(container) > 0;
  }

  @Override
  public boolean canReceive() {
    if(container.stackSize > 1) {
      return false;
    }
    return item.getMaxInput(container) > 0;
  }

}