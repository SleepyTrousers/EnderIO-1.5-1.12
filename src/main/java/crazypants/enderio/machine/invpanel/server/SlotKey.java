package crazypants.enderio.machine.invpanel.server;

public final class SlotKey {

    final AbstractInventory inv;
    final int slot;
    final ItemEntry item;
    int count;

    SlotKey(AbstractInventory inv, int slot, ItemEntry item, int count) {
        this.inv = inv;
        this.slot = slot;
        this.item = item;
        this.count = count;
    }

    void remove(InventoryDatabaseServer db) {
        item.removeSlot(this);
        db.entryChanged(item);
    }
}
