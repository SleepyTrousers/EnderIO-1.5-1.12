package crazypants.enderio.base.invpanel.database;

import javax.annotation.Nonnull;

public final class SlotKey {
  public final AbstractInventory inv;
  public final int slot;
  public final IServerItemEntry item;
  public int count;

  SlotKey(@Nonnull AbstractInventory inv, int slot, @Nonnull IServerItemEntry key, int count) {
    this.inv = inv;
    this.slot = slot;
    this.item = key;
    this.count = count;
  }

  void remove(@Nonnull IInventoryDatabaseServer db) {
    item.removeSlot(this);
    db.entryChanged(item);
  }
}
