package crazypants.enderio.machine.vacuum;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class InventoryFilterUpgrade implements IInventory {

  private final TileVacuumChest te;

  public InventoryFilterUpgrade(TileVacuumChest te) {
    this.te = te;
  }

  @Override
  public int getSizeInventory() {
    return 1;
  }

  @Override
  public ItemStack getStackInSlot(int slot) {
    if(slot == 0) {
      return te.getFilterItem();
    }
    return null;
  }

  @Override
  public ItemStack decrStackSize(int slot, int num) {
    if(slot == 0) {
      ItemStack current = te.getFilterItem();      
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
      te.setFilterItem(remaining);
      return result;
    }

    return null;
  }

  @Override
  public ItemStack getStackInSlotOnClosing(int i) {
    return null;
  }

  @Override
  public void setInventorySlotContents(int slot, ItemStack is) {
    if(slot == 0) {
      te.setFilterItem(is);
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
  public boolean isUseableByPlayer(EntityPlayer ep) {
    return true;
  }

  @Override
  public void openInventory() {
  }

  @Override
  public void closeInventory() {
  }

  @Override
  public boolean isItemValidForSlot(int slot, ItemStack is) {
    return slot == 0 && te.isItemValidForFilter(is);
  }
}
