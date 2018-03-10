package crazypants.enderio.conduits.conduit.item;

public final class SlotKey {
  public final AbstractInventory inv;
  public final int slot;
  public final IServerItemEntry item;
  public int count;

  SlotKey(AbstractInventory inv, int slot, IServerItemEntry key, int count) {
    this.inv = inv;
    this.slot = slot;
    this.item = key;
    this.count = count;
  }

  void remove(IInventoryDatabaseServer db) {
    item.removeSlot(this);
    db.entryChanged(item);
  }
}
