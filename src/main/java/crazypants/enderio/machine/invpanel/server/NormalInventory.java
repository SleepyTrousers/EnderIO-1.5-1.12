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
    if (stack == null || stack.stackSize == 0 || db.lookupItem(stack, entry, false) != entry) {
      return 0;
    }
    ItemStack extracted = inv.extractItem(slot, count, false);
    if (extracted == null || extracted.stackSize == 0) {
      return 0;
    }    
    ni.onItemExtracted(slot, extracted.stackSize);

    stack = inv.getStackInSlot(slot);
    if (stack != null) {
      updateCount(db, slot, entry, stack.stackSize);
    } else {
      updateCount(db, slot, entry, 0);
    }

    return extracted.stackSize;
  }

  @Override
  protected void markForScanning(BlockPos pos) {
    if (ni.isAt(pos)) {
      markForScanning();
    }
  }

}
