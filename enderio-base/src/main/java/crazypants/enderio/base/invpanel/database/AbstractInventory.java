package crazypants.enderio.base.invpanel.database;

import javax.annotation.Nonnull;

import crazypants.enderio.base.EnderIO;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

public abstract class AbstractInventory {
  static final SlotKey[] NO_SLOTS = new SlotKey[0];
  protected SlotKey[] slotKeys = NO_SLOTS;

  protected long nextScan = -1;
  protected boolean taggedForScanning = false; // block has send a TE update notification and should be scanned
  protected float taggingbias = 0; // high: block will send TE update notifications, low: block must be polled
  protected boolean inScan = false;

  /**
   * Mark this inventory as being in need of a scan.
   */
  protected void markForScanning() {
    taggedForScanning = true;
  }

  /**
   * Mark this inventory as being in need of a scan if the given parameter is the inventory's location
   * 
   * @param pos
   *          A changed location.
   */
  public void markForScanning(@Nonnull BlockPos pos) {
  }

  /**
   * Checks if this inventory should be scanned for changes now.
   * 
   * @param now
   *          The current tick (EnderIO.proxy.getTickCount())
   * @return true if it should be scanned either because the data is stale or because it sent a notification about changes.
   */
  public boolean shouldBeScannedNow(long now) {
    return taggedForScanning || nextScan <= now;
  }

  /**
   * Finish the inventory scan and reset the timer.
   */
  public void markScanned() {
    nextScan = EnderIO.proxy.getServerTickCount() + Math.max(Math.min(1 + (slotKeys.length + 8) / 9, 20 * 60), 30);
    if (taggingbias > 50) {
      nextScan += 2 * 60 * 20; // 2m
    } else if (taggingbias > 5) {
      nextScan += 15 * 20; // 15s
    }
    taggedForScanning = false;
    inScan = false;
  }

  /**
   * Called during a scan when a change in the inventory has been detected. This will determine if the inventory sends notifications or not.
   */
  private void onChangeFound() {
    if (!inScan) {
      inScan = true;
      if (taggedForScanning) {
        taggingbias += 1;
      } else {
        taggingbias *= 0.5;
      }
    }
  }

  protected void setEmpty(@Nonnull IInventoryDatabaseServer db) {
    if (slotKeys.length != 0) {
      reset(db, 0);
    }
  }

  protected void reset(@Nonnull IInventoryDatabaseServer db, int count) {
    for (SlotKey slotKey : slotKeys) {
      if (slotKey != null) {
        slotKey.remove(db);
      }
    }
    slotKeys = new SlotKey[count];
  }

  protected void updateSlot(@Nonnull IInventoryDatabaseServer db, int slot, @Nonnull ItemStack stack) {
    if (stack.isEmpty()) {
      emptySlot(db, slot);
    } else {
      updateSlot(db, slot, stack, stack.getCount());
    }
  }

  protected void updateSlot(@Nonnull IInventoryDatabaseServer db, int slot, @Nonnull ItemStack stack, int count) {
    SlotKey slotKey = slotKeys[slot];
    IServerItemEntry current = slotKey != null ? slotKey.item : null;
    IServerItemEntry key = db.lookupItem(stack, current, true);
    if (key != current) {
      onChangeFound();
      updateSlotKey(db, slot, slotKey, key, count);
    } else if (slotKey != null && slotKey.count != count) {
      onChangeFound();
      slotKey.count = count;
      db.entryChanged(current);
    }
  }

  protected void emptySlot(@Nonnull IInventoryDatabaseServer db, int slot) {
    SlotKey slotKey = slotKeys[slot];
    if (slotKey != null) {
      onChangeFound();
      slotKey.remove(db);
      slotKeys[slot] = null;
    }
  }

  private void updateSlotKey(@Nonnull IInventoryDatabaseServer db, int slot, SlotKey slotKey, IServerItemEntry key, int count) {
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

  protected void updateCount(@Nonnull IInventoryDatabaseServer db, int slot, IServerItemEntry entry, int count) {
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

  public abstract int scanInventory(@Nonnull IInventoryDatabaseServer db);

  public abstract int extractItem(@Nonnull IInventoryDatabaseServer db, IServerItemEntry entry, int slot, int count);

}
