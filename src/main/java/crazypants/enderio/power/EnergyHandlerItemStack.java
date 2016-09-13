package crazypants.enderio.power;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.energy.IEnergyStorage;

public class EnergyHandlerItemStack implements IEnergyStorage {

  public static final String KEY_CAP = "energyEIO";
  public static final String KEY_ENERGY = "energy";
  private static final String KEY_CAPACITY = "capacity";
  private static final String KEY_MAX_IN = "maxInput";
  private static final String KEY_MAX_OUT = "maxOuput";

  protected final ItemStack container;
  protected int capacity;
  private int energy;
  protected int maxInput;
  protected int maxOutput;

  public EnergyHandlerItemStack(ItemStack container) {
    this.container = container;

    NBTTagCompound tag = getCapRoot(container);
    capacity = tag.getInteger(KEY_CAPACITY);
    energy = tag.getInteger(KEY_ENERGY);
    maxInput = tag.getInteger(KEY_MAX_IN);
    maxOutput = tag.getInteger(KEY_MAX_OUT);
  }

  private NBTTagCompound getCapRoot(ItemStack cont) {
    if (!cont.hasTagCompound()) {
      cont.setTagCompound(new NBTTagCompound());
    }
    NBTTagCompound root = cont.getTagCompound();
    NBTTagCompound res = root.getCompoundTag(KEY_CAP);
    if (res == null) {
      res = new NBTTagCompound();
      root.setTag(KEY_CAP, res);
    }
    return res;
  }

  @Override
  public int receiveEnergy(int maxReceive, boolean simulate) {
    if (!canReceive()) {
      return 0;
    }
    int energyReceived = Math.min(capacity - energy, Math.min(maxInput, maxReceive));
    if (!simulate) {
      energy += energyReceived;
      getCapRoot(container).setInteger(KEY_ENERGY, energy);
    }
    return energyReceived;
  }

  @Override
  public int extractEnergy(int maxExtract, boolean simulate) {
    if(!canExtract()) {
      return 0;
    }
    int energyExtracted = Math.min(energy, Math.min(maxOutput, maxExtract));
    if(!simulate) {
      energy -= energyExtracted;
      getCapRoot(container).setInteger(KEY_ENERGY, energy);
    }
    return energyExtracted;
  }

  @Override
  public int getEnergyStored() {
    return energy;
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

}