package crazypants.util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import com.enderio.core.common.util.Util;

public class ArrayInventory implements IInventory {

  protected ItemStack[] items;

  public ArrayInventory(ItemStack... items) {
    this.items = items;
  }

  public ArrayInventory(int size) {
    items = new ItemStack[size];
  }

  @Override
  public int getSizeInventory() {
    return items.length;
  }

  @Override
  public ItemStack getStackInSlot(int slot) {
    return items[slot];
  }

  @Override
  public ItemStack decrStackSize(int slot, int amount) {
    return Util.decrStackSize(this, slot, amount);
  }

  @Override
  public ItemStack getStackInSlotOnClosing(int slot) {
    return null;
  }

  @Override
  public void setInventorySlotContents(int slot, ItemStack stack) {
    items[slot] = stack;
    markDirty();
  }

  @Override
  public int getInventoryStackLimit() {
    return 64;
  }

  @Override
  public boolean isUseableByPlayer(EntityPlayer var1) {
    return true;
  }

  @Override
  public boolean isItemValidForSlot(int i, ItemStack itemstack) {
    return true;
  }

  @Override
  public String getInventoryName() {
    return "ArrayInventory";
  }

  @Override
  public boolean hasCustomInventoryName() {
    return false;
  }

  @Override
  public void markDirty() {

  }

  @Override
  public void openInventory() {

  }

  @Override
  public void closeInventory() {

  }
}
