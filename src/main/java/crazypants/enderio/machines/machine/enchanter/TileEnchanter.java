package crazypants.enderio.machines.machine.enchanter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.Util;

import crazypants.enderio.base.machine.base.te.AbstractMachineEntity;
import crazypants.enderio.base.machine.modes.IoMode;
import crazypants.enderio.base.recipe.MachineRecipeInput;
import crazypants.enderio.base.recipe.MachineRecipeRegistry;
import crazypants.enderio.base.recipe.enchanter.EnchanterRecipe;
import crazypants.enderio.machines.init.MachineObject;
import info.loenwind.autosave.annotations.Storable;
import info.loenwind.autosave.annotations.Store;
import info.loenwind.autosave.handlers.minecraft.HandleItemStack.HandleItemStackNNList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

@Storable
public class TileEnchanter extends AbstractMachineEntity implements ISidedInventory {

  @Store(handler = HandleItemStackNNList.class)
  private NNList<ItemStack> inv = new NNList<>(4, ItemStack.EMPTY);

  @Override
  public boolean isUsableByPlayer(@Nonnull EntityPlayer player) {
    return canPlayerAccess(player);
  }

  @Override
  public int getSizeInventory() {
    return inv.size();
  }

  @Override
  public @Nonnull ItemStack getStackInSlot(int slot) {
    if (slot < 0 || slot >= inv.size()) {
      return ItemStack.EMPTY;
    }
    return inv.get(slot);
  }

  @Override
  public @Nonnull ItemStack decrStackSize(int slot, int amount) {
    return Util.decrStackSize(this, slot, amount);
  }

  @Override
  public void setInventorySlotContents(int slot, @Nullable ItemStack contents) {
    if (contents == null) {
      inv.set(slot, contents);
    } else {
      inv.set(slot, contents.copy());
    }
    if (contents != null && contents.getCount() > getInventoryStackLimit()) {
      contents.setCount(getInventoryStackLimit());
    }
  }

  @Override
  public @Nonnull ItemStack removeStackFromSlot(int index) {
    ItemStack res = getStackInSlot(index);
    setInventorySlotContents(index, res);
    return res;
  }

  @Override
  public void clear() {
    for (int i = 0; i < inv.size(); ++i) {
      inv.set(i, ItemStack.EMPTY);
    }
  }

  @Override
  public @Nonnull String getName() {
    return MachineObject.block_enchanter.getUnlocalisedName();
  }

  @Override
  public boolean hasCustomName() {
    return false;
  }

  @Override
  public int getInventoryStackLimit() {
    return 64;
  }

  @Override
  public void openInventory(@Nonnull EntityPlayer p) {
  }

  @Override
  public void closeInventory(@Nonnull EntityPlayer p) {
  }

  @Override
  public boolean isItemValidForSlot(int slot, @Nonnull ItemStack stack) {
    if (stack.isEmpty()) {
      return false;
    }
    return !MachineRecipeRegistry.instance.getRecipesForInput(MachineRecipeRegistry.ENCHANTER, new MachineRecipeInput(slot, stack)).isEmpty();
  }

  @Override
  public boolean isEmpty() {
    return inv.stream().allMatch(ItemStack::isEmpty);
  }

  public EnchanterRecipe getCurrentEnchantmentRecipe() {
    if (inv.get(0).isEmpty() || inv.get(1).isEmpty() || inv.get(2).isEmpty()) {
      return null;
    }
    return (EnchanterRecipe) MachineRecipeRegistry.instance.getRecipeForInputs(MachineRecipeRegistry.ENCHANTER, getInvAsMachineRecipeInput());
  }

  public int getCurrentEnchantmentCost() {
    final EnchanterRecipe currentEnchantmentRecipe = getCurrentEnchantmentRecipe();
    return currentEnchantmentRecipe != null ? currentEnchantmentRecipe.getXPCost(getInvAsMachineRecipeInput()) : 0;
  }

  public @Nonnull MachineRecipeInput[] getInvAsMachineRecipeInput() {
    return new MachineRecipeInput[] { new MachineRecipeInput(0, inv.get(0)), new MachineRecipeInput(1, inv.get(1)), new MachineRecipeInput(2, inv.get(2)) };
  }

  public void setOutput(@Nonnull ItemStack output) {
    inv.set(inv.size() - 1, output);
    markDirty();
  }

  @Override
  public @Nonnull int[] getSlotsForFace(@Nonnull EnumFacing side) {
    return new int[0];
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
  public boolean canInsertItem(int index, @Nonnull ItemStack itemStackIn, @Nonnull EnumFacing direction) {
    return false;
  }

  @Override
  public boolean canExtractItem(int index, @Nonnull ItemStack stack, @Nonnull EnumFacing direction) {
    return false;
  }

  @Override
  @Nonnull
  public String getMachineName() {
    return MachineRecipeRegistry.ENCHANTER;
  }

  @Override
  public boolean isActive() {
    return false;
  }

  @Override
  protected boolean doPull(EnumFacing dir) {
    return false;
  }

  @Override
  protected boolean doPush(EnumFacing dir) {
    return false;
  }

  @Override
  protected boolean processTasks(boolean redstoneCheck) {
    return false; // never called
  }

  @Override
  public void doUpdate() {
    disableTicking();
  }

  @Override
  public boolean supportsMode(@Nullable EnumFacing faceHit, @Nullable IoMode mode) {
    return mode == IoMode.NONE;
  }

}
