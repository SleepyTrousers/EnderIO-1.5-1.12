package crazypants.util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;

public class InventoryWrapper implements ISidedInventory {

  public static ISidedInventory asSidedInventory(IInventory inv) {
    if(inv == null) {
      return null;
    }
    if(inv instanceof ISidedInventory) {
      return (ISidedInventory) inv;
    }
    return new InventoryWrapper(inv);
  }

  private IInventory inv;

  public InventoryWrapper(IInventory inventory) {
    this.inv = ItemUtil.getInventory(inventory);
  }

  public IInventory getWrappedInv() {
    return inv;
  }

  @Override
  public int getSizeInventory() {
    return inv.getSizeInventory();
  }

  @Override
  public ItemStack getStackInSlot(int slot) {
    if(slot < 0 || slot >= inv.getSizeInventory()) {
      return null;
    }
    return inv.getStackInSlot(slot);
  }

  @Override
  public ItemStack decrStackSize(int slot, int amount) {
    return inv.decrStackSize(slot, amount);
  }

  @Override
  public ItemStack getStackInSlotOnClosing(int slot) {
    return inv.getStackInSlotOnClosing(slot);
  }

  @Override
  public void setInventorySlotContents(int slot, ItemStack itemStack) {
    inv.setInventorySlotContents(slot, itemStack);
  }

  @Override
  public String getInventoryName() {
    return inv.getInventoryName();
  }

  @Override
  public int getInventoryStackLimit() {
    return inv.getInventoryStackLimit();
  }

  @Override
  public void markDirty() {
    inv.markDirty();
  }

  @Override
  public boolean isUseableByPlayer(EntityPlayer entityplayer) {
    return inv.isUseableByPlayer(entityplayer);
  }

  @Override
  public void openInventory() {
    inv.openInventory();
  }

  @Override
  public void closeInventory() {
    inv.closeInventory();
  }

  @Override
  public boolean isItemValidForSlot(int slot, ItemStack itemStack) {
    return inv.isItemValidForSlot(slot, itemStack);
  }

  @Override
  public int[] getAccessibleSlotsFromSide(int var1) {
    int[] slots = new int[inv.getSizeInventory()];
    for (int i = 0; i < slots.length; i++) {
      slots[i] = i;
    }
    return slots;
  }

  @Override
  public boolean canInsertItem(int slot, ItemStack itemStack, int side) {
    return isItemValidForSlot(slot, itemStack);
  }

  @Override
  public boolean canExtractItem(int slot, ItemStack itemStack, int side) {
    return true;
  }

  @Override
  public boolean hasCustomInventoryName() {
    return false;
  }
}