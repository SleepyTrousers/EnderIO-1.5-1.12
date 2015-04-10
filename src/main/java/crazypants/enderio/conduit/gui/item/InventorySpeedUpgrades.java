package crazypants.enderio.conduit.gui.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;
import crazypants.enderio.conduit.item.IItemConduit;
import crazypants.enderio.init.EIOItems;

public class InventorySpeedUpgrades implements IInventory {

  IItemConduit itemConduit;
  ForgeDirection dir;
  
  public InventorySpeedUpgrades(IItemConduit itemConduit, ForgeDirection dir) {
    this.itemConduit = itemConduit;
    this.dir = dir;
  }
  
  @Override
  public int getSizeInventory() {
    return 1;
  }

  @Override
  public ItemStack getStackInSlot(int slot) {
    if(slot == 0) {
      return itemConduit.getSpeedUpgrade(dir);
    }
    return null;
  }

  @Override
  public ItemStack decrStackSize(int slot, int num) {
    if(slot == 0) {
      ItemStack current = itemConduit.getSpeedUpgrade(dir);
      if(current == null) {
        return current;
      }
      ItemStack result;
      ItemStack remaining;
      if(num >= current.stackSize) {
        result = current.copy();
        remaining = null;
      } else {
        result = current.copy();
        result.stackSize = num;
        remaining = current.copy();
        remaining.stackSize -= num;
      }
      itemConduit.setSpeedUpgrade(dir, remaining);
      return result;
    }

    return null;
  }

  @Override
  public ItemStack getStackInSlotOnClosing(int var1) {
    return null;
  }

  @Override
  public void setInventorySlotContents(int slot, ItemStack var2) {
    if(slot == 0) {
      itemConduit.setSpeedUpgrade(dir, var2);
    }
  }

  @Override
  public String getInventoryName() {
    return "SpeedUpgrade";
  }

  @Override
  public boolean hasCustomInventoryName() {
    return false;
  }

  @Override
  public int getInventoryStackLimit() {
    return 15;
  }

  @Override
  public void markDirty() {      
  }

  @Override
  public boolean isUseableByPlayer(EntityPlayer var1) {
    return true;
  }

  @Override
  public void openInventory() {
  }

  @Override
  public void closeInventory() {
  }

  @Override
  public boolean isItemValidForSlot(int var1, ItemStack item) {
    if(item == null) {
      return false;
    }
    return item.getItem() == EIOItems.itemExtractSpeedUpgrade;
  }

}