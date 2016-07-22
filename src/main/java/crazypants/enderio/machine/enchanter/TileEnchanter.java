package crazypants.enderio.machine.enchanter;

import javax.annotation.Nullable;

import com.enderio.core.common.util.Util;

import crazypants.enderio.ModObject;
import crazypants.enderio.TileEntityEio;
import info.loenwind.autosave.annotations.Storable;
import info.loenwind.autosave.annotations.Store;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

@Storable
public class TileEnchanter extends TileEntityEio implements ISidedInventory {

  @Store
  private ItemStack[] inv = new ItemStack[4];

  @Store
  private EnumFacing facing = EnumFacing.NORTH;

  public void setFacing(EnumFacing s) {
    facing = s;
  }

  public EnumFacing getFacing() {
    return facing;
  }

  @Override
  public boolean isUseableByPlayer(EntityPlayer player) {
    return canPlayerAccess(player);
  }

  @Override
  public int getSizeInventory() {
    return inv.length;
  }

  @Override
  public ItemStack getStackInSlot(int slot) {
    if (slot < 0 || slot >= inv.length) {
      return null;
    }
    return inv[slot];
  }

  @Override
  public ItemStack decrStackSize(int slot, int amount) {
    return Util.decrStackSize(this, slot, amount);
  }

  @Override
  public void setInventorySlotContents(int slot, @Nullable ItemStack contents) {
    if (contents == null) {
      inv[slot] = contents;
    } else {
      inv[slot] = contents.copy();
    }
    if (contents != null && contents.stackSize > getInventoryStackLimit()) {
      contents.stackSize = getInventoryStackLimit();
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
    for(int i=0;i<inv.length;++i) {
      inv[i] = null;
    }
  }

  @Override
  public String getName() {
    return ModObject.blockEnchanter.getUnlocalisedName();
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
    if (stack == null) {
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

  public EnchanterRecipe getCurrentEnchantmentRecipe() {
    if (inv[0] == null) {
      return null;
    }
    if (inv[1] == null) {
      return null;
    }
    if (inv[2] == null) {
      return null;
    }
    EnchanterRecipe ench = EnchanterRecipeManager.getInstance().getEnchantmentRecipeForInput(inv[1]);
    if (ench == null) {
      return null;
    }
    int level = ench.getLevelForStackSize(inv[1].stackSize);
    if (level <= 0) {
      return null;
    }
    if ((inv[2].stackSize) < ench.getLapizForStackSize(inv[1].stackSize)) {
      return null;
    }
    return ench;
  }

  public EnchantmentData getCurrentEnchantmentData() {
    EnchanterRecipe rec = getCurrentEnchantmentRecipe();
    if (rec == null) {
      return null;
    }
    int level = rec.getLevelForStackSize(inv[1].stackSize);
    return new EnchantmentData(rec.getEnchantment(), level);
  }

  public int getCurrentEnchantmentCost() {
    return getEnchantmentCost(getCurrentEnchantmentRecipe());
  }

  private int getEnchantmentCost(EnchanterRecipe currentEnchantment) {
    ItemStack item = inv[1];
    if (item == null) {
      return 0;
    }
    if (currentEnchantment == null) {
      return 0;
    }    
    int level = currentEnchantment.getLevelForStackSize(item.stackSize);
    return currentEnchantment.getCostForLevel(level);
  }



  public void setOutput(ItemStack output) {
    inv[inv.length - 1] = output;
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

}
