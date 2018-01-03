package crazypants.enderio.machine.invpanel.server;

class DSUInventory { 
//extends AbstractInventory {
//  final IDeepStorageUnit dsu;
//
//  DSUInventory(IDeepStorageUnit dsu) {
//    this.dsu = dsu;
//    this.slotKeys = new SlotKey[1];
//  }
//
//  @Override
//  public int scanInventory(InventoryDatabaseServer db) {
//    ItemStack stack = dsu.getStoredItemType();
//    updateSlot(db, 0, stack);
//    return 1;
//  }
//
//  @Override
//  public int extractItem(InventoryDatabaseServer db, ItemEntry entry, int slot, int count) {
//    ItemStack stack = dsu.getStoredItemType();
//    if (db.lookupItem(stack, entry, false) != entry) {
//      return 0;
//    }
//    int remaining = stack.getCount();
//    if (count > remaining) {
//      count = remaining;
//    }
//    remaining -= count;
//    dsu.setStoredItemCount(remaining);
//    updateCount(db, 0, entry, remaining);
//    return count;
//  }

}
