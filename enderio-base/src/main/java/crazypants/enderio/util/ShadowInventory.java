package crazypants.enderio.util;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.Util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;

public class ShadowInventory implements IInventory {
  private final ItemStack[] items;
  private final IInventory master;

  public ShadowInventory(IInventory master) {
    this.master = master;
    items = new ItemStack[master.getSizeInventory()];
  }

  @Override
  public int getSizeInventory() {
    return master.getSizeInventory();
  }

  @Override
  public @Nonnull ItemStack getStackInSlot(int index) {
    final ItemStack itemStack = items[index];
    return itemStack == null ? master.getStackInSlot(index) : itemStack;
  }

  @Override
  public @Nonnull ItemStack decrStackSize(int index, int count) {
    return Util.decrStackSize(this, index, count);
  }

  @Override
  public void setInventorySlotContents(int index, @Nonnull ItemStack stack) {
    items[index] = stack;
  }

  @Override
  public int getInventoryStackLimit() {
    return master.getInventoryStackLimit();
  }

  @Override
  public void markDirty() {
  }

  @Override
  public boolean isItemValidForSlot(int index, @Nonnull ItemStack stack) {
    return master.isItemValidForSlot(index, stack);
  }

  @Override
  public @Nonnull String getName() {
    return master.getName();
  }

  @Override
  public boolean hasCustomName() {
    return master.hasCustomName();
  }

  @Override
  public @Nonnull ITextComponent getDisplayName() {
    return master.getDisplayName();
  }

  @Override
  public @Nonnull ItemStack removeStackFromSlot(int index) {
    ItemStack stack = getStackInSlot(index);
    setInventorySlotContents(index, Prep.getEmpty());
    return stack;
  }

  @Override
  public void openInventory(@Nonnull EntityPlayer player) {
    master.openInventory(player);
  }

  @Override
  public void closeInventory(@Nonnull EntityPlayer player) {
    master.closeInventory(player);
  }

  @Override
  public int getField(int id) {
    return master.getField(id);
  }

  @Override
  public void setField(int id, int value) {
    master.setField(id, value);

  }

  @Override
  public int getFieldCount() {
    return master.getFieldCount();
  }

  @Override
  public void clear() {
    for (int i = 0; i < items.length; i++) {
      removeStackFromSlot(i);
    }
  }

  @Override
  public boolean isEmpty() {
    for (int i = 0; i < items.length; i++) {
      if (!getStackInSlot(i).isEmpty()) {
        return false;
      }
    }
    return true;
  }

  @Override
  public boolean isUsableByPlayer(@Nonnull EntityPlayer player) {
    return master.isUsableByPlayer(player);
  }

}
