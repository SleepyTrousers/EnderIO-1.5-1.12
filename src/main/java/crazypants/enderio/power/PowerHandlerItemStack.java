package crazypants.enderio.power;

import javax.annotation.Nullable;

import crazypants.util.NbtValue;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

public class PowerHandlerItemStack implements IEnergyStorage, ICapabilityProvider {

  protected final ItemStack container;
  protected int capacity;
  protected int maxInput;
  protected int maxOutput;

  public PowerHandlerItemStack(ItemStack container, int capacity, int maxInput, int maxOutput) {
    this.container = container;
    this.capacity = capacity;
    this.maxInput = maxInput;
    this.maxOutput = maxOutput;
  }

  @Override
  public int receiveEnergy(int maxReceive, boolean simulate) {
    if (!canReceive()) {
      return 0;
    }
    int energy = getEnergyStored();
    int energyReceived = Math.min(capacity - energy, Math.min(maxInput, maxReceive));
    if (!simulate) {
      energy += energyReceived;
      setEnergyStored(energy);
    }
    return energyReceived;
  }

  @Override
  public int extractEnergy(int maxExtract, boolean simulate) {
    if(!canExtract()) {
      return 0;
    }
    int energy = getEnergyStored();
    int energyExtracted = Math.min(energy, Math.min(maxOutput, maxExtract));
    if(!simulate) {
      energy -= energyExtracted;
      setEnergyStored(energy);
    }
    return energyExtracted;
  }

  public void setEnergyStored(int energy) {
    NbtValue.ENERGY.setInt(container, MathHelper.clamp_int(energy, 0, capacity));
  }
  
  @Override
  public int getEnergyStored() {
    return NbtValue.ENERGY.getInt(container);
  }

  @Override
  public int getMaxEnergyStored() {
    return capacity;
  }

  @Override
  public boolean canExtract() {
    return maxOutput > 0;
  }

  @Override
  public boolean canReceive() {
    return maxInput > 0;
  }
  
  @Override
  public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
    return capability == CapabilityEnergy.ENERGY;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
    return capability == CapabilityEnergy.ENERGY ? (T) this : null;
  }

}