package crazypants.enderio.machine.invpanel.server;

import net.minecraft.item.ItemStack;

abstract class AbstractInventory {
    static final SlotKey[] NO_SLOTS = new SlotKey[0];
    SlotKey[] slotKeys = NO_SLOTS;

    protected void setEmpty(InventoryDatabaseServer db) {
        if (slotKeys.length != 0) {
            reset(db, 0);
        }
    }

    protected void reset(InventoryDatabaseServer db, int count) {
        for (SlotKey slotKey : slotKeys) {
            if (slotKey != null) {
                slotKey.remove(db);
            }
        }
        slotKeys = new SlotKey[count];
    }

    protected void updateSlot(InventoryDatabaseServer db, int slot, ItemStack stack) {
        if (stack == null) {
            emptySlot(db, slot);
        } else {
            updateSlot(db, slot, stack, stack.stackSize);
        }
    }

    protected void updateSlot(InventoryDatabaseServer db, int slot, ItemStack stack, int count) {
        SlotKey slotKey = slotKeys[slot];
        ItemEntry current = slotKey != null ? slotKey.item : null;
        ItemEntry key = db.lookupItem(stack, current, true);
        if (key != current) {
            updateSlotKey(db, slot, slotKey, key, count);
        } else if (slotKey != null && slotKey.count != count) {
            slotKey.count = count;
            db.entryChanged(current);
        }
    }

    protected void emptySlot(InventoryDatabaseServer db, int slot) {
        SlotKey slotKey = slotKeys[slot];
        if (slotKey != null) {
            slotKey.remove(db);
            slotKeys[slot] = null;
        }
    }

    private void updateSlotKey(InventoryDatabaseServer db, int slot, SlotKey slotKey, ItemEntry key, int count) {
        if (slotKey != null) {
            slotKey.remove(db);
            slotKey = null;
        }
        if (key != null) {
            slotKey = new SlotKey(this, slot, key, count);
            key.addSlot(slotKey);
            db.entryChanged(key);
        }
        slotKeys[slot] = slotKey;
    }

    protected void updateCount(InventoryDatabaseServer db, int slot, ItemEntry entry, int count) {
        SlotKey slotKey = slotKeys[slot];
        if (slotKey != null && slotKey.count != count && slotKey.item == entry) {
            if (count == 0) {
                slotKey.remove(db);
                slotKeys[slot] = null;
            } else {
                slotKey.count = count;
                db.entryChanged(slotKey.item);
            }
        }
    }

    abstract int scanInventory(InventoryDatabaseServer db);

    abstract int extractItem(InventoryDatabaseServer db, ItemEntry entry, int slot, int count);
}
