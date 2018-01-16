package crazypants.enderio.conduit.gui.item;

import com.enderio.core.common.inventory.EnderInventory;
import crazypants.enderio.base.filter.IItemFilterUpgrade;
import crazypants.enderio.conduit.item.IItemConduit;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;

import javax.annotation.Nonnull;

import static crazypants.enderio.conduit.init.ConduitObject.item_extract_speed_upgrade;

/**
 * The Inventory for Holding Conduit Upgrades
 */
public class InventoryUpgrades extends EnderInventory {

  IItemConduit itemConduit;
  EnumFacing dir;

  public InventoryUpgrades(IItemConduit itemConduit, EnumFacing dir) {
    this.itemConduit = itemConduit;
    this.dir = dir;
  }

  @Override
  public int getSizeInventory() {
    return 4;
  }

  @Override
  public boolean isEmpty() {
    return false;
  }

  @Override
  @Nonnull
  public ItemStack getStackInSlot(int slot) {
    switch (slot) {
    case 0:
      return itemConduit.getSpeedUpgrade(dir);
      // TODO Inventory
//    case 1:
//      return itemConduit.getFunctionUpgrade(dir);
    case 2:
      return itemConduit.getInputFilterUpgrade(dir);
    case 3:
      return itemConduit.getOutputFilterUpgrade(dir);
    default:
      return null;
    }
  }

  @Override
  @Nonnull
  public ItemStack decrStackSize(int slot, int num) {
    ItemStack current = getStackInSlot(slot);
    if (current.isEmpty()) {
      return current;
    }
    ItemStack result;
    ItemStack remaining;
    if (num >= current.getCount()) {
      result = current.copy();
      remaining = ItemStack.EMPTY;
    } else {
      result = current.copy();
      result.setCount(num);
      remaining = current.copy();
      remaining.shrink(num);
    }
    setInventorySlotContents(slot, remaining);
    return result;
  }

  @Override
  public void setInventorySlotContents(int slot, @Nonnull ItemStack stack) {
    switch (slot) {
    case 0:
      itemConduit.setSpeedUpgrade(dir, stack);
      break;
      // TODO Inventory
//    case 1:
//      itemConduit.setFunctionUpgrade(dir, stack);
//      break;
    case 2:
      itemConduit.setInputFilterUpgrade(dir, stack);
      break;
    case 3:
      itemConduit.setOutputFilterUpgrade(dir, stack);
      break;
    }
  }

  @Override
  public void clear() {
    for (int i = 0; i < 4; i++) {
      setInventorySlotContents(i, ItemStack.EMPTY);
    }
  }

  @Override
  @Nonnull
  public ItemStack removeStackFromSlot(int index) {
    ItemStack res = getStackInSlot(index);
    setInventorySlotContents(index, ItemStack.EMPTY);
    return res;
  }

  @Override
  @Nonnull
  public String getName() {
    return "Upgrades";
  }

  @Override
  public boolean hasCustomName() {
    return false;
  }

  @Override
  public int getInventoryStackLimit() {
    return 15;
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
  public void closeInventory(EntityPlayer e) {
  }

  @Override
  public boolean isItemValidForSlot(int slot, @Nonnull ItemStack item) {
    if (item.isEmpty()) {
      return false;
    }
    switch (slot) {
    case 0:
      return item.getItem() == item_extract_speed_upgrade.getItem();
      // TODO Inventory
//    case 1:
//      final FunctionUpgrade functionUpgrade = ItemFunctionUpgrade.getFunctionUpgrade(item);
//      return functionUpgrade != null
//          && (functionUpgrade != FunctionUpgrade.INVENTORY_PANEL || !itemConduit.isConnectedToNetworkAwareBlock(dir));
    case 2:
    case 3:
      return item.getItem() instanceof IItemFilterUpgrade;
    }
    return false;
  }

  @Override
  @Nonnull
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