package crazypants.enderio.conduit.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;
import crazypants.enderio.conduit.item.IItemConduit;
import crazypants.enderio.conduit.item.IItemFilterUpgrade;

public class InventoryFilterUpgrade implements IInventory {

  final IItemConduit itemConduit;
  final ForgeDirection dir;
  final boolean isInput;
  
  InventoryFilterUpgrade(IItemConduit itemConduit, ForgeDirection dir, boolean isInput) {
    this.itemConduit = itemConduit;
    this.dir = dir;
    this.isInput = isInput;
  }
  
  @Override
  public int getSizeInventory() {
    return 1;
  }

  @Override
  public ItemStack getStackInSlot(int slot) {
    if(slot == 0) {
      if(isInput) {
        return itemConduit.getInputFilterUpgrade(dir);
      } else {
        return itemConduit.getOutputFilterUpgrade(dir);
      }
    }
    return null;
  }

  @Override
  public ItemStack decrStackSize(int slot, int num) {
    if(slot == 0) {
      ItemStack current = getStackInSlot(0);      
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
      if(isInput) {
        itemConduit.setInputFilterUpgrade(dir, remaining);
      } else {
        itemConduit.setOutputFilterUpgrade(dir, remaining);
      }
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
      if(isInput) {
        itemConduit.setInputFilterUpgrade(dir, var2);
      } else {
        itemConduit.setOutputFilterUpgrade(dir, var2);
      }
    }
  }

  @Override
  public String getInventoryName() {
    return "FilterUpgrade";
  }

  @Override
  public boolean hasCustomInventoryName() {
    return false;
  }

  @Override
  public int getInventoryStackLimit() {
    return 1;
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
    return item.getItem() instanceof IItemFilterUpgrade;
  }


}
