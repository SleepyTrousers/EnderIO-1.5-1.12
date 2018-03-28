package crazypants.enderio.util;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.Util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;

public class ShadowInventory implements IInventory {
  private final NNList<ItemStack> items;
  private final IInventory master;

  public ShadowInventory(IInventory master) {
    this.master = master;
    items = new NNList<ItemStack>(master.getSizeInventory(), ItemStack.EMPTY);
  }

  @Override
  public int getSizeInventory() {
    return master.getSizeInventory();
  }

  @Override
  public @Nonnull ItemStack getStackInSlot(int index) {
    final ItemStack itemStack = items.get(index);
    return itemStack.isEmpty() ? master.getStackInSlot(index) : itemStack;
  }

  @Override
  public @Nonnull ItemStack decrStackSize(int index, int count) {
    return Util.decrStackSize(this, index, count);
  }

  @Override
  public void setInventorySlotContents(int index, @Nonnull ItemStack stack) {
    items.set(index, stack);
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
    items.clear();
  }

  @Override
  public boolean isEmpty() {
    return items.isEmpty();
  }

  @Override
  public boolean isUsableByPlayer(@Nonnull EntityPlayer player) {
    return master.isUsableByPlayer(player);
  }

}
