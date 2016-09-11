package crazypants.enderio.machine.invpanel.server;

import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerGroup;
import net.minecraft.item.ItemStack;

public class DrawerGroupInventory extends AbstractInventory {
  final IDrawerGroup dg;

  public DrawerGroupInventory(IDrawerGroup dg) {
    this.dg = dg;
  }

  @Override
  int scanInventory(InventoryDatabaseServer db) {
    int count = dg.getDrawerCount();
    if (count == 0) {
      setEmpty(db);
      return 0;
    }
    if (count != slotKeys.length) {
      reset(db, count);
    }
    for (int i = 0; i < count; i++) {
      IDrawer drawer = dg.getDrawer(i);
      ItemStack stack;
      int quantity;
      if (drawer != null &&
              (quantity = drawer.getStoredItemCount()) > 0 &&
              (stack = drawer.getStoredItemPrototype()) != null &&
              drawer.canItemBeExtracted(stack)) {
        updateSlot(db, i, stack, quantity);
      } else {
        emptySlot(db, i);
      }
    }
    return count;
  }

  @Override
  int extractItem(InventoryDatabaseServer db, ItemEntry entry, int slot, int count) {
    if (slot >= dg.getDrawerCount()) {
      return 0;
    }
    IDrawer drawer = dg.getDrawer(slot);
    if (drawer == null) {
      return 0;
    }
    int remaining = drawer.getStoredItemCount();
    if (remaining <= 0) {
      return 0;
    }
    ItemStack stack = drawer.getStoredItemPrototype();
    if (db.lookupItem(stack, entry, false) != entry) {
      return 0;
    }
    if (count > remaining) {
      count = remaining;
    }
    remaining -= count;
    drawer.setStoredItemCount(remaining);
    updateCount(db, slot, entry, remaining);
    return count;
  }

}
