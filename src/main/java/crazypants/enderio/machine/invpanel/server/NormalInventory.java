package crazypants.enderio.machine.invpanel.server;

import crazypants.enderio.conduit.item.NetworkedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.IItemHandler;

class NormalInventory extends AbstractInventory {
  final NetworkedInventory ni;

  NormalInventory(NetworkedInventory ni) {
    this.ni = ni;
  }

  @Override
  int scanInventory(InventoryDatabaseServer db) {
    IItemHandler inv = ni.getInventory();
    
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
      if (stack != null) {
        if (stack.stackSize == 0) {
          // empty but type-restricted slot
          stack = null;
        } else {
          // HL: I'm not sure why we double check the slot's content here
          ItemStack extracted = inv.extractItem(slot, stack.stackSize, true);
          if (extracted == null) {
            stack = null;
          } else if (stack.stackSize > stack.getMaxStackSize()) {
            // big storage
            if (extracted.stackSize < stack.getMaxStackSize()) {
              stack = null;
            }
          } else if (extracted.stackSize != stack.stackSize) {
            stack = null;
          }
        }
      }
      updateSlot(db, slot, stack);
    }
    return numSlots;
  }

  @Override
  public int extractItem(InventoryDatabaseServer db, ItemEntry entry, int slot, int count) {
    IItemHandler inv = ni.getInventory();
    if (inv == null) {
      return 0;
    }
    ItemStack stack = inv.getStackInSlot(slot);
    if (stack == null || stack.stackSize == 0) {
      return 0;
    } else if (stack.stackSize > stack.getMaxStackSize()) {
      stack = stack.copy();
      stack.stackSize = stack.getMaxStackSize();
    }
    ItemStack extracted = inv.extractItem(slot, stack.stackSize, true);
    if(extracted == null || extracted.stackSize != stack.stackSize) {
      return 0;
    }    
    if (db.lookupItem(stack, entry, false) != entry) {
      return 0;
    }
    int remaining = stack.stackSize;
    if (count > remaining) {
      count = remaining;
    }
    ni.itemExtracted(slot, count);
    remaining -= count;
    updateCount(db, slot, entry, remaining);
    return count;
  }

  @Override
  protected void markForScanning(BlockPos pos) {
    if (ni.isAt(pos)) {
      markForScanning();
    }
  }

}
