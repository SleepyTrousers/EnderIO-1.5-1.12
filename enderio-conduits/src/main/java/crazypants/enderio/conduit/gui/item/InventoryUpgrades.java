package crazypants.enderio.conduit.gui.item;

import crazypants.enderio.base.filter.IItemFilterUpgrade;
import crazypants.enderio.conduit.item.IItemConduit;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;

import static crazypants.enderio.conduit.init.ConduitObject.item_extract_speed_upgrade;

/**
 * The Inventory for Holding Conduit Upgrades
 */
public class InventoryUpgrades implements IItemHandler {

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
//    case 1:
//      return itemConduit.getFunctionUpgrade(dir);
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
    return ItemStack.EMPTY;
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
    setInventorySlotContents(slot, remaining);
    return result;
  }

  private void setInventorySlotContents(int slot, @Nonnull ItemStack stack) {
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
  public int getSlots() {
    return 4;
  }

  @Override
  public int getSlotLimit(int slot) {
    return slot == 1 ? 15 : 1;
  }

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

}