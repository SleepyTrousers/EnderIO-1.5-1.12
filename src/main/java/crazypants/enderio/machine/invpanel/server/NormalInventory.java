package crazypants.enderio.machine.invpanel.server;

import crazypants.enderio.conduit.item.NetworkedInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

class NormalInventory extends AbstractInventory {
  final NetworkedInventory ni;

  NormalInventory(NetworkedInventory ni) {
    this.ni = ni;
  }

  @Override
  int scanInventory(InventoryDatabaseServer db) {
    IItemHandler inv = ni.getInventory();
    
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
        ItemStack extracted = inv.extractItem(slot, stack.stackSize, true);
        if(extracted == null || extracted.stackSize != stack.stackSize) {
          stack = null;  
        }        
      }
      updateSlot(db, slot, stack);
    }
    return numSlots;
  }

  @Override
  public int extractItem(InventoryDatabaseServer db, ItemEntry entry, int slot, int count) {
    IItemHandler inv = ni.getInventory();        
    ItemStack stack = inv.getStackInSlot(slot);
    if (stack == null) {
      return 0;
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

}
