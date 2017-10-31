package crazypants.enderio.conduit.gui.item;

import javax.annotation.Nullable;

import crazypants.enderio.conduit.item.FunctionUpgrade;
import crazypants.enderio.conduit.item.IItemConduit;
import crazypants.enderio.conduit.item.ItemFunctionUpgrade;
import crazypants.enderio.conduit.item.filter.IItemFilterUpgrade;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;

import static crazypants.enderio.ModObject.itemExtractSpeedUpgrade;

public class InventoryUpgrades implements IInventory {

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
  public ItemStack getStackInSlot(int slot) {
    switch (slot) {
    case 0:
      return itemConduit.getSpeedUpgrade(dir);
    case 1:
      return itemConduit.getFunctionUpgrade(dir);
    case 2:
      return itemConduit.getInputFilterUpgrade(dir);
    case 3:
      return itemConduit.getOutputFilterUpgrade(dir);
    default:
      return null;
    }
  }

  @Override
  public ItemStack decrStackSize(int slot, int num) {
    ItemStack current = getStackInSlot(slot);
    if (current == null) {
      return current;
    }
    ItemStack result;
    ItemStack remaining;
    if (num >= current.stackSize) {
      result = current.copy();
      remaining = null;
    } else {
      result = current.copy();
      result.stackSize = num;
      remaining = current.copy();
      remaining.stackSize -= num;
    }
    setInventorySlotContents(slot, remaining);
    return result;
  }

  @Override
  public void setInventorySlotContents(int slot, @Nullable ItemStack var2) {
    switch (slot) {
    case 0:
      itemConduit.setSpeedUpgrade(dir, var2);
      break;
    case 1:
      itemConduit.setFunctionUpgrade(dir, var2);
      break;
    case 2:
      itemConduit.setInputFilterUpgrade(dir, var2);
      break;
    case 3:
      itemConduit.setOutputFilterUpgrade(dir, var2);
      break;
    }
  }

  @Override
  public void clear() {
    for (int i = 0; i < 4; i++) {
      setInventorySlotContents(i, null);
    }
  }

  @Override
  public ItemStack removeStackFromSlot(int index) {
    ItemStack res = getStackInSlot(index);
    setInventorySlotContents(index, null);
    return res;
  }

  @Override
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
  public boolean isUseableByPlayer(EntityPlayer var1) {
    return true;
  }

  @Override
  public void openInventory(EntityPlayer e) {
  }

  @Override
  public void closeInventory(EntityPlayer e) {
  }

  @Override
  public boolean isItemValidForSlot(int slot, ItemStack item) {
    if (item == null) {
      return false;
    }
    switch (slot) {
    case 0:
      return item.getItem() == itemExtractSpeedUpgrade.getItem();
    case 1:
      final FunctionUpgrade functionUpgrade = ItemFunctionUpgrade.getFunctionUpgrade(item);
      return functionUpgrade != null
          && (functionUpgrade != FunctionUpgrade.INVENTORY_PANEL || !itemConduit.isConnectedToNetworkAwareBlock(dir));
    case 2:
    case 3:
      return item.getItem() instanceof IItemFilterUpgrade;
    }
    return false;
  }

  @Override
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