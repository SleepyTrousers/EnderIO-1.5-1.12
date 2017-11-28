package crazypants.enderio.machine.enchanter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.Util;

import crazypants.enderio.machine.MachineObject;
import crazypants.enderio.machine.base.te.AbstractMachineEntity;
import crazypants.enderio.machine.modes.IoMode;
import info.loenwind.autosave.annotations.Storable;
import info.loenwind.autosave.annotations.Store;
import info.loenwind.autosave.handlers.minecraft.HandleItemStack.HandleItemStackNNList;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
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
  public boolean isUsableByPlayer(EntityPlayer player) {
    return canPlayerAccess(player);
  }

  @Override
  public int getSizeInventory() {
    return inv.size();
  }

  @Override
  public ItemStack getStackInSlot(int slot) {
    if (slot < 0 || slot >= inv.size()) {
      return ItemStack.EMPTY;
    }
    return inv.get(slot);
  }

  @Override
  public ItemStack decrStackSize(int slot, int amount) {
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
  public ItemStack removeStackFromSlot(int index) {
    ItemStack res = getStackInSlot(index);
    setInventorySlotContents(index, res);
    return res;
  }
  
  @Override
  public void clear() {       
    for(int i=0;i<inv.size();++i) {
      inv.set(i, ItemStack.EMPTY);
    }
  }

  @Override
  public String getName() {
    return MachineObject.blockEnchanter.getUnlocalisedName();
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
  public void openInventory(EntityPlayer p) {
  }

  @Override
  public void closeInventory(EntityPlayer p) {
  }

  @Override
  public boolean isItemValidForSlot(int slot, ItemStack stack) {
    if (stack.isEmpty()) {
      return false;
    }
    if (slot == 0) {
      return Items.WRITABLE_BOOK == stack.getItem();
    }
    if (slot == 1) {
      return EnchanterRecipeManager.getInstance().getEnchantmentRecipeForInput(stack) != null;
    }
    if (slot == 2) {
      return stack.getItem() == Items.DYE && stack.getMetadata() == 4;
    }
    return false;
  }
  
  @Override
  public boolean isEmpty() {
    return inv.stream().allMatch(ItemStack::isEmpty);
  }

  public EnchanterRecipe getCurrentEnchantmentRecipe() {
    if (inv.get(0).isEmpty() || inv.get(1).isEmpty() || inv.get(2).isEmpty()) {
      return null;
    }
    EnchanterRecipe ench = EnchanterRecipeManager.getInstance().getEnchantmentRecipeForInput(inv.get(1));
    if (ench == null) {
      return null;
    }
    int level = ench.getLevelForStackSize(inv.get(1).getCount());
    if (level <= 0) {
      return null;
    }
    if ((inv.get(2).getCount()) < ench.getLapizForStackSize(inv.get(1).getCount())) {
      return null;
    }
    return ench;
  }

  public EnchantmentData getCurrentEnchantmentData() {
    EnchanterRecipe rec = getCurrentEnchantmentRecipe();
    if (rec == null) {
      return null;
    }
    int level = rec.getLevelForStackSize(inv.get(1).getCount());
    return new EnchantmentData(rec.getEnchantment(), level);
  }

  public int getCurrentEnchantmentCost() {
    return getEnchantmentCost(getCurrentEnchantmentRecipe());
  }

  private int getEnchantmentCost(EnchanterRecipe currentEnchantment) {
    ItemStack item = inv.get(1);
    if (item.isEmpty()) {
      return 0;
    }
    if (currentEnchantment == null) {
      return 0;
    }    
    int level = currentEnchantment.getLevelForStackSize(item.getCount());
    return currentEnchantment.getCostForLevel(level);
  }



  public void setOutput(@Nonnull ItemStack output) {
    inv.set(inv.size() - 1, output);
    markDirty();
  }

  @Override
  public int[] getSlotsForFace(EnumFacing side) {
    return new int[0];
  }

  @Override
  public ITextComponent getDisplayName() {
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
  public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {   
    return false;
  }

  @Override
  public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
    return false;
  }

  @Override
  @Nonnull
  public String getMachineName() {
    return MachineObject.blockEnchanter.getUnlocalisedName();
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
