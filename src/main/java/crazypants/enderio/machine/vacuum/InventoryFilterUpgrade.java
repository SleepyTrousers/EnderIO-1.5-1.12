package crazypants.enderio.machine.vacuum;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;

import javax.annotation.Nullable;

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
    if (slot == 0) {
      return te.getFilterItem();
    }
    return null;
  }

  @Override
  public ItemStack decrStackSize(int slot, int num) {
    if (slot == 0) {
      ItemStack current = te.getFilterItem();
      if (current == null) {
        return current;
      }
      ItemStack result;
      ItemStack remaining;
      if (num >= current.stackSize) {
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
  public void setInventorySlotContents(int slot, @Nullable ItemStack is) {
    if (slot == 0) {
      te.setFilterItem(is);
    }
  }

  @Override
  public void clear() {
    te.setFilterItem(null);
  }

  @Override
  public ItemStack removeStackFromSlot(int index) {
    ItemStack res = te.getFilterItem();
    te.setFilterItem(null);
    return res;
  }

  @Override
  public String getName() {
    return "FilterUpgrade";
  }

  @Override
  public boolean hasCustomName() {
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
  public void openInventory(EntityPlayer player) {
  }

  @Override
  public void closeInventory(EntityPlayer player) {
  }

  @Override
  public boolean isItemValidForSlot(int slot, ItemStack is) {
    return slot == 0 && te.isItemValidForFilter(is);
  }

  @Override
  public ITextComponent getDisplayName() {
    return hasCustomName() ? new TextComponentString(getName()) : new TextComponentTranslation(getName(), new Object[0]);
  }

  @Override
  public int getField(int id) {
    return 0;
  }

  @Override
  public void setField(int id, int value) {
  }

  @Override
  public int getFieldCount() {
    return 0;
  }

}
