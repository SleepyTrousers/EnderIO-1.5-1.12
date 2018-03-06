package crazypants.enderio.conduit.gui.item;

import javax.annotation.Nonnull;

import crazypants.enderio.base.filter.IItemFilterUpgrade;
import crazypants.enderio.conduit.item.IItemConduit;
import crazypants.enderio.conduit.item.ItemExtractSpeedUpgrade;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.IItemHandlerModifiable;

/**
 * The Inventory for Holding Conduit Upgrades
 */
public class InventoryUpgrades implements IItemHandlerModifiable {

  private IItemConduit itemConduit;
  private @Nonnull EnumFacing dir;

  public InventoryUpgrades(@Nonnull IItemConduit itemConduit, @Nonnull EnumFacing dir) {
    this.itemConduit = itemConduit;
    this.dir = dir;
  }

  @Override
  @Nonnull
  public ItemStack getStackInSlot(int slot) {
    switch (slot) {
    case 0:
      return itemConduit.getSpeedUpgrade(dir);
    // TODO Inventory
    // case 1:
    // return itemConduit.getFunctionUpgrade(dir);
    case 2:
      return itemConduit.getInputFilterUpgrade(dir);
    case 3:
      return itemConduit.getOutputFilterUpgrade(dir);
    default:
      return ItemStack.EMPTY;
    }
  }

  @Nonnull
  @Override
  public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
    if (!isItemValidForSlot(slot, stack)) {
      return stack;
    }

    ItemStack slotStack = stack.splitStack(getSlotLimit(slot));

    if (!simulate) {
      setInventorySlotContents(slot, slotStack);
    }
    return stack;
  }

  @Nonnull
  @Override
  public ItemStack extractItem(int slot, int amount, boolean simulate) {
    ItemStack current = getStackInSlot(slot);
    if (current.isEmpty()) {
      return current;
    }
    ItemStack result;
    ItemStack remaining;
    if (amount >= current.getCount()) {
      result = current.copy();
      remaining = ItemStack.EMPTY;
    } else {
      result = current.copy();
      result.setCount(amount);
      remaining = current.copy();
      remaining.shrink(amount);
    }

    if (!simulate) {
      setInventorySlotContents(slot, remaining);
    }
    return result;
  }

  private void setInventorySlotContents(int slot, @Nonnull ItemStack stack) {
    switch (slot) {
    case 0:
      itemConduit.setSpeedUpgrade(dir, stack);
      break;
    // TODO Inventory
    // case 1:
    // itemConduit.setFunctionUpgrade(dir, stack);
    // break;
    case 2:
      itemConduit.setInputFilterUpgrade(dir, stack);
      break;
    case 3:
      itemConduit.setOutputFilterUpgrade(dir, stack);
      break;
    }
  }

  private boolean isItemValidForSlot(int slot, @Nonnull ItemStack stack) {
    if (stack.isEmpty()) {
      return false;
    }
    switch (slot) {
    case 0:
      return stack.getItem() instanceof ItemExtractSpeedUpgrade;
    // TODO Inventory
    // case 1:
    // final FunctionUpgrade functionUpgrade = ItemFunctionUpgrade.getFunctionUpgrade(item);
    // return functionUpgrade != null
    // && (functionUpgrade != FunctionUpgrade.INVENTORY_PANEL || !itemConduit.isConnectedToNetworkAwareBlock(dir));
    case 2:
      return stack.getItem() instanceof IItemFilterUpgrade;
    case 3:
      return stack.getItem() instanceof IItemFilterUpgrade;
    }
    return false;
  }

  @Override
  public int getSlots() {
    return 4;
  }

  @Override
  public int getSlotLimit(int slot) {
    return slot == 0 ? 15 : 1;
  }

  @Override
  public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
    setInventorySlotContents(slot, stack);
  }

}