package crazypants.enderio.machine.invpanel.server;

import net.minecraft.item.ItemStack;

abstract class AbstractInventory {
  static final ItemEntry[] NO_SLOTS = new ItemEntry[0];
  ItemEntry[] slotItems = NO_SLOTS;
  int[] itemCounts;

  protected void setEmpty(InventoryDatabaseServer db, int aiIndex) {
    if (slotItems.length != 0) {
      reset(db, 0, aiIndex);
    }
  }

  protected void reset(InventoryDatabaseServer db, int count, int aiIndex) {
    for (int slot = 0; slot < slotItems.length; slot++) {
      ItemEntry key = slotItems[slot];
      if (key != null) {
        key.removeSlot(aiIndex, slot);
        db.entryChanged(key);
      }
    }
    slotItems = new ItemEntry[count];
    itemCounts = new int[count];
  }

  protected void updateSlot(InventoryDatabaseServer db, int slot, int aiIndex, ItemStack stack) {
    ItemEntry current = slotItems[slot];
    if (stack == null || stack.stackSize <= 0) {
      if (current != null) {
        slotItems[slot] = null;
        itemCounts[slot] = 0;
        current.removeSlot(aiIndex, slot);
        db.entryChanged(current);
      }
    } else {
      ItemEntry key = db.lookupItem(stack, current, true);
      if (key != current) {
        slotItems[slot] = key;
        itemCounts[slot] = stack.stackSize;
        key.addSlot(aiIndex, slot);
        db.entryChanged(key);
        if (current != null) {
          current.removeSlot(aiIndex, slot);
          db.entryChanged(current);
        }
      } else if (itemCounts[slot] != stack.stackSize) {
        itemCounts[slot] = stack.stackSize;
        db.entryChanged(current);
      }
    }
  }

  protected void updateCount(InventoryDatabaseServer db, int slot, int aiIndex, ItemEntry entry, int count) {
    if (itemCounts[slot] != count) {
      itemCounts[slot] = count;
      if (count == 0) {
        slotItems[slot] = null;
        entry.removeSlot(aiIndex, slot);
      }
      db.entryChanged(entry);
    }
  }

  abstract void scanInventory(InventoryDatabaseServer db, int aiIndex);

  abstract int extractItem(InventoryDatabaseServer db, ItemEntry entry, int slot, int aiIndex, int count);

}
