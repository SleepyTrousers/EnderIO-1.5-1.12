package crazypants.enderio.machines.machine.crafter;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.util.Prep;
import info.loenwind.autosave.annotations.Storable;
import info.loenwind.autosave.annotations.Store;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

@Storable
public class DummyCraftingGrid implements IInventory {

  @Store
  ItemStack[] inv = new ItemStack[10];

  public boolean hasValidRecipe() {
    return getOutput() != null;
  }

  public ItemStack getOutput() {
    return inv[9];
  }

  @Override
  public int getSizeInventory() {
    return inv.length;
  }

  @Override
  public @Nonnull ItemStack getStackInSlot(int var1) {
    if (var1 < 0 || var1 >= inv.length) {
      return Prep.getEmpty();
    }
    return NullHelper.first(inv[var1], Prep.getEmpty());
  }

  @Override
  public @Nonnull ItemStack decrStackSize(int fromSlot, int amount) {
    ItemStack item = inv[fromSlot];
    inv[fromSlot] = null;
    if (item == null) {
      return Prep.getEmpty();
    }
    item.setCount(0);
    return item;
  }

  @Override
  public void setInventorySlotContents(int i, @Nonnull ItemStack itemstack) {
    inv[i] = itemstack.copy();
    if (i < 9) {
      inv[i].setCount(0); // FIXME 1.11
    }
  }

  @Override
  public void clear() {
    for (int i = 0; i < inv.length; i++) {
      inv[i] = null;
    }
  }

  @Override
  public @Nonnull ItemStack removeStackFromSlot(int index) {
    ItemStack res = getStackInSlot(index);
    setInventorySlotContents(index, Prep.getEmpty());
    return res;
  }

  @Override
  public @Nonnull String getName() {
    return "CraftingGrid";
  }

  @Override
  public boolean hasCustomName() {
    return false;
  }

  @Override
  public int getInventoryStackLimit() {
    return 0;
  }

  @Override
  public void markDirty() {
  }

  @Override
  public boolean isUsableByPlayer(@Nonnull EntityPlayer var1) {
    return true;
  }

  @Override
  public void openInventory(@Nonnull EntityPlayer e) {
  }

  @Override
  public void closeInventory(@Nonnull EntityPlayer e) {
  }

  @Override
  public boolean isItemValidForSlot(int var1, @Nonnull ItemStack var2) {
    return var1 < 9;
  }

  @Override
  public @Nonnull ITextComponent getDisplayName() {
    return new TextComponentString(getName());
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

  @Override
  public boolean isEmpty() {
    return true;
  }
}
