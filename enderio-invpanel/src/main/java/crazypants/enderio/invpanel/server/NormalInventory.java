package crazypants.enderio.invpanel.server;

import javax.annotation.Nonnull;

import crazypants.enderio.base.invpanel.database.AbstractInventory;
import crazypants.enderio.base.invpanel.database.IInventoryDatabaseServer;
import crazypants.enderio.base.invpanel.database.IServerItemEntry;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.IItemHandler;

class NormalInventory extends AbstractInventory {
  final IItemHandler inv;
  final BlockPos pos;

  NormalInventory(@Nonnull IItemHandler inv, @Nonnull BlockPos pos) {
    this.inv = inv;
    this.pos = pos;
  }

  @Override
  public int scanInventory(@Nonnull IInventoryDatabaseServer db) {
    if (inv == null) {
      setEmpty(db);
      return 0;
    }
    int numSlots = inv.getSlots();
    if (numSlots < 1) {
      setEmpty(db);
      return 0;
    }
    if (numSlots != slotKeys.length) {
      reset(db, numSlots);
    }
    for (int slot = 0; slot < numSlots; slot++) {
      ItemStack stack = inv.getStackInSlot(slot);
      if (!stack.isEmpty()) {
        if (stack.getCount() == 0) {
          // empty but type-restricted slot
          stack = ItemStack.EMPTY;
        } else {
          // HL: I'm not sure why we double check the slot's content here
          ItemStack extracted = inv.extractItem(slot, stack.getCount(), true);
          if (extracted.isEmpty()) {
            stack = ItemStack.EMPTY;
          } else if (stack.getCount() > stack.getMaxStackSize()) {
            // big storage
            if (extracted.getCount() < stack.getMaxStackSize()) {
              stack = ItemStack.EMPTY;
            }
          } else if (extracted.getCount() != stack.getCount()) {
            stack = ItemStack.EMPTY;
          }
        }
      }
      updateSlot(db, slot, stack);
    }
    return numSlots;
  }

  @Override
  public int extractItem(@Nonnull IInventoryDatabaseServer db, IServerItemEntry entry, int slot, int count) {
    if (inv == null) {
      return 0;
    }
    ItemStack stack = inv.getStackInSlot(slot);
    if (stack.isEmpty() || stack.getCount() == 0 || db.lookupItem(stack, entry, false) != entry) {
      return 0;
    }
    ItemStack extracted = inv.extractItem(slot, count, false);
    if (extracted.isEmpty() || extracted.getCount() == 0) {
      return 0;
    }

    stack = inv.getStackInSlot(slot);
    if (!stack.isEmpty()) {
      updateCount(db, slot, entry, stack.getCount());
    } else {
      updateCount(db, slot, entry, 0);
    }

    return extracted.getCount();
  }

  @Override
  public void markForScanning(@Nonnull BlockPos posIn) {
    if (pos.equals(posIn)) {
      markForScanning();
    }
  }

}
