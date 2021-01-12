package crazypants.enderio.invpanel.server;

import javax.annotation.Nonnull;

import crazypants.enderio.base.Log;
import crazypants.enderio.base.invpanel.database.AbstractInventory;
import crazypants.enderio.base.invpanel.database.IInventoryDatabaseServer;
import crazypants.enderio.base.invpanel.database.IServerItemEntry;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.IItemHandler;

class NormalInventory extends AbstractInventory {

  private final @Nonnull IItemHandler inv;
  private final @Nonnull BlockPos pos;
  private boolean errored = false;

  NormalInventory(@Nonnull IItemHandler inv, @Nonnull BlockPos pos) {
    this.inv = inv;
    this.pos = pos;
  }

  @Override
  public int scanInventory(@Nonnull IInventoryDatabaseServer db) {
    int numSlots = errored ? 0 : inv.getSlots();
    if (numSlots < 1) {
      setEmpty(db);
      return 0;
    }
    if (numSlots != slotKeys.length) {
      reset(db, numSlots);
    }
    for (int slot = 0; slot < numSlots; slot++) {
      scanSlot(db, slot);
    }
    return numSlots;
  }

  protected void scanSlot(@Nonnull IInventoryDatabaseServer db, int slot) {
    ItemStack stack = inv.getStackInSlot(slot);
    if (!stack.isEmpty()) {
      if (stack.getCount() == 0) {
        // empty but type-restricted slot
        stack = ItemStack.EMPTY;
      } else {
        // check if we actually could extract this slot's content
        ItemStack extracted = inv.extractItem(slot, stack.getCount(), true);
        if (extracted.isEmpty()) {
          stack = ItemStack.EMPTY;
        } else if (extracted.getItem() != stack.getItem()) {
          // inventory lied to us about that slot's content
          stack = ItemStack.EMPTY;
        } else if (stack.getCount() > stack.getMaxStackSize()) {
          // big storage
          if (extracted.getCount() < extracted.getMaxStackSize()) {
            // the inventory advertises plenty of items but doesn't deliver a full stack?
            // not sure why it would do that, but let's decide that the advertisement was wrong and the simulation was right
            stack = extracted.copy();
          }
        } else if (extracted.getCount() != stack.getCount()) {
          stack = ItemStack.EMPTY;
        }
      }
    }
    updateSlot(db, slot, stack);
  }

  @Override
  public int extractItem(@Nonnull IInventoryDatabaseServer db, IServerItemEntry entry, int slot, int count) {
    ItemStack stack = inv.extractItem(slot, count, true);
    if (stack.isEmpty() || db.lookupItem(stack, entry, false) != entry) {
      return 0;
    }
    ItemStack extracted = inv.extractItem(slot, count, false);
    if (extracted.isEmpty()) {
      Log.error("Inventory at " + pos + " implements IItemHandler wrong. extractItem() with and without simulation give different results.");
      // what else could be wrong with this inventory? It could dupe or void items left and right! We don't want to touch something like that...
      setEmpty(db);
      errored = true;
      return 0;
    }

    scanSlot(db, slot);

    return extracted.getCount();
  }

  @Override
  public void markForScanning(@Nonnull BlockPos posIn) {
    if (pos.equals(posIn)) {
      markForScanning();
    }
  }

}
