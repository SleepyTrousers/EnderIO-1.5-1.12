package crazypants.enderio.machine.invpanel.server;

import net.minecraft.item.ItemStack;
import powercrystals.minefactoryreloaded.api.IDeepStorageUnit;

class DSUInventory extends AbstractInventory {
  final IDeepStorageUnit dsu;

  DSUInventory(IDeepStorageUnit dsu) {
    this.dsu = dsu;
  }

  @Override
  public int scanInventory(InventoryDatabaseServer db, int aiIndex) {
    if (slotItems.length != 1) {
      reset(db, 1, aiIndex);
    }
    ItemStack stack = dsu.getStoredItemType();
    updateSlot(db, 0, aiIndex, stack);
    return 1;
  }

  @Override
  public int extractItem(InventoryDatabaseServer db, ItemEntry entry, int slot, int aiIndex, int count) {
    if (slotItems.length != 1) {
      return 0;
    }
    ItemStack stack = dsu.getStoredItemType();
    if (db.lookupItem(stack, entry, false) != entry) {
      return 0;
    }
    int remaining = stack.stackSize;
    if (count > remaining) {
      count = remaining;
    }
    remaining -= count;
    dsu.setStoredItemCount(remaining);
    updateCount(db, slot, aiIndex, entry, remaining);
    return count;
  }

}
