package crazypants.enderio.power;

import cofh.api.energy.IEnergyContainerItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.energy.IEnergyStorage;

public class ItemWrapperRF implements IEnergyStorage {

  IEnergyContainerItem itemRF;
  
  ItemStack container;
  
  public ItemWrapperRF(IEnergyContainerItem itemRF, ItemStack container) {
    this.itemRF = itemRF;
    this.container = container;
  }

  @Override
  public int receiveEnergy(int maxReceive, boolean simulate) {
    return itemRF.receiveEnergy(container, maxReceive, simulate);
  }

  @Override
  public int extractEnergy(int maxExtract, boolean simulate) {
    return itemRF.extractEnergy(container, maxExtract, simulate);
  }

  @Override
  public int getEnergyStored() {
    return itemRF.getEnergyStored(container);
  }

  @Override
  public int getMaxEnergyStored() {
    return itemRF.getMaxEnergyStored(container);
  }

  @Override
  public boolean canExtract() {
    return true;
  }

  @Override
  public boolean canReceive() {
    return true;
  }

}
