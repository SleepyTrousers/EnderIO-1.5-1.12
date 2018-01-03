package crazypants.enderio.machine.invpanel.server;

import crazypants.enderio.base.EnderIO;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

abstract class AbstractInventory {
  static final SlotKey[] NO_SLOTS = new SlotKey[0];
  SlotKey[] slotKeys = NO_SLOTS;

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
  protected void markForScanning(BlockPos pos) {
  }

  /**
   * Checks if this inventory should be scanned for changes now.
   * 
   * @param now
   *          The current tick (EnderIO.proxy.getTickCount())
   * @return true if it should be scanned either because the data is stale or because it sent a notification about changes.
   */
  protected boolean shouldBeScannedNow(long now) {
    return taggedForScanning || nextScan <= now;
  }

  /**
   * Finish the inventory scan and reset the timer.
   */
  protected void markScanned() {
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

  protected void setEmpty(InventoryDatabaseServer db) {
    if (slotKeys.length != 0) {
      reset(db, 0);
    }
  }

  protected void reset(InventoryDatabaseServer db, int count) {
    for (SlotKey slotKey : slotKeys) {
      if(slotKey != null) {
        slotKey.remove(db);
      }
    }
    slotKeys = new SlotKey[count];
  }

  protected void updateSlot(InventoryDatabaseServer db, int slot, ItemStack stack) {
    if (stack == null) {
      emptySlot(db, slot);
    } else {
      updateSlot(db, slot, stack, stack.getCount());
    }
  }

  protected void updateSlot(InventoryDatabaseServer db, int slot, ItemStack stack, int count) {
    SlotKey slotKey = slotKeys[slot];
    ItemEntry current = slotKey != null ? slotKey.item : null;
    ItemEntry key = db.lookupItem(stack, current, true);
    if (key != current) {
      onChangeFound();
      updateSlotKey(db, slot, slotKey, key, count);
    } else if (slotKey != null && slotKey.count != count) {
      onChangeFound();
      slotKey.count = count;
      db.entryChanged(current);
    }
  }

  protected void emptySlot(InventoryDatabaseServer db, int slot) {
    SlotKey slotKey = slotKeys[slot];
    if (slotKey != null) {
      onChangeFound();
      slotKey.remove(db);
      slotKeys[slot] = null;
    }
  }

  private void updateSlotKey(InventoryDatabaseServer db, int slot, SlotKey slotKey, ItemEntry key, int count) {
    if (slotKey != null) {
      slotKey.remove(db);
      slotKey = null;
    }
    if (key != null)  {
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
