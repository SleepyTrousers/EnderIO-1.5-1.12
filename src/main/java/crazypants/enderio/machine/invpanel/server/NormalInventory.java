package crazypants.enderio.machine.invpanel.server;

import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;

import crazypants.enderio.conduit.item.NetworkedInventory;

class NormalInventory extends AbstractInventory {

    final NetworkedInventory ni;

    NormalInventory(NetworkedInventory ni) {
        this.ni = ni;
    }

    @Override
    int scanInventory(InventoryDatabaseServer db) {
        ISidedInventory inv = ni.getInventoryRecheck();
        int side = ni.getInventorySide();
        int[] slotIndices = inv.getAccessibleSlotsFromSide(side);
        if (slotIndices == null || slotIndices.length == 0) {
            setEmpty(db);
            return 0;
        }
        int count = slotIndices.length;
        if (count != slotKeys.length) {
            reset(db, count);
        }
        for (int slot = 0; slot < count; slot++) {
            int invSlot = slotIndices[slot];
            ItemStack stack = inv.getStackInSlot(invSlot);
            if (stack != null && !inv.canExtractItem(invSlot, stack, side)) {
                stack = null;
            }
            updateSlot(db, slot, stack);
        }
        return count;
    }

    @Override
    public int extractItem(InventoryDatabaseServer db, ItemEntry entry, int slot, int count) {
        ISidedInventory inv = ni.getInventoryRecheck();
        int side = ni.getInventorySide();
        int[] slotIndices = inv.getAccessibleSlotsFromSide(side);
        if (slotIndices == null || slot >= slotIndices.length) {
            return 0;
        }
        int invSlot = slotIndices[slot];
        ItemStack stack = inv.getStackInSlot(invSlot);
        if (stack == null || !inv.canExtractItem(invSlot, stack, side)) {
            return 0;
        }
        if (db.lookupItem(stack, entry, false) != entry) {
            return 0;
        }
        int remaining = stack.stackSize;
        if (count > remaining) {
            count = remaining;
        }
        ni.itemExtracted(invSlot, count);
        remaining -= count;
        updateCount(db, slot, entry, remaining);
        return count;
    }
}
