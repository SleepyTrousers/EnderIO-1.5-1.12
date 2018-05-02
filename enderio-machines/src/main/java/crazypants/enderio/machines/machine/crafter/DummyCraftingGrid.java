package crazypants.enderio.machines.machine.crafter;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.util.Prep;
import info.loenwind.autosave.annotations.Storable;
import info.loenwind.autosave.annotations.Store;
import info.loenwind.autosave.handlers.minecraft.HandleItemStack;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

@Storable
public class DummyCraftingGrid implements IInventory {

  @Store(handler = HandleItemStack.HandleItemStackNNList.class)
  NNList<ItemStack> inv = new NNList<ItemStack>(10, ItemStack.EMPTY);

  public boolean hasValidRecipe() {
    return !getOutput().isEmpty();
  }

  @Nonnull
  public ItemStack getOutput() {
    return inv.get(9);
  }

  @Override
  public int getSizeInventory() {
    return inv.size();
  }

  @Override
  @Nonnull
  public ItemStack getStackInSlot(int slot) {
    if (slot < 0 || slot >= inv.size()) {
      return Prep.getEmpty();
    }
    return NullHelper.first(inv.get(slot), Prep.getEmpty());
  }

  @Override
  @Nonnull
  public ItemStack decrStackSize(int fromSlot, int amount) {
    ItemStack item = inv.get(fromSlot);
    inv.set(fromSlot, ItemStack.EMPTY);
    if (item.isEmpty()) {
      return Prep.getEmpty();
    }
    item.setCount(0);
    return item;
  }

  @Override
  public void setInventorySlotContents(int i, @Nonnull ItemStack itemstack) {
    inv.set(i, itemstack.copy());
    // if (i < 9) {
    // inv.get(i).setCount(0);
    // }
  }

  @Override
  public void clear() {
    inv.clear();
  }

  @Override
  @Nonnull
  public ItemStack removeStackFromSlot(int index) {
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
