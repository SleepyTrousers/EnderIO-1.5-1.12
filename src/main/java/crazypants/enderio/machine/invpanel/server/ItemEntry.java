package crazypants.enderio.machine.invpanel.server;

import crazypants.enderio.machine.invpanel.ItemEntryBase;
import java.util.HashSet;
import net.minecraft.nbt.NBTTagCompound;

public class ItemEntry extends ItemEntryBase {
  private final HashSet<Integer> slots = new HashSet<Integer>();

  public ItemEntry(int dbID, int hash, int itemID, int meta, NBTTagCompound nbt) {
    super(dbID, hash, itemID, meta, nbt);
  }

  static int encodeAISlot(int aiIndex, int slot) {
    return (aiIndex << 12) | slot;
  }

  void addSlot(int aiIndex, int slot) {
    slots.add(encodeAISlot(aiIndex, slot));
  }

  void removeSlot(int aiIndex, int slot) {
    slots.remove(encodeAISlot(aiIndex, slot));
  }

  int countItems(InventoryDatabaseServer db) {
    int count = 0;
    for (Integer idx : slots) {
      AbstractInventory key = db.getInventory(idx >> 12);
      count += key.itemCounts[idx & 4095];
    }
    return count;
  }

  int extractItems(InventoryDatabaseServer db, int count) {
    int extracted = 0;
    Integer[] copy = slots.toArray(new Integer[slots.size()]);
    for (Integer idx : copy) {
      int aiIndex = idx >> 12;
      int slotIndex = idx & 4095;
      AbstractInventory key = db.getInventory(aiIndex);
      int amount = key.extractItem(db, this, slotIndex, aiIndex, count);
      count -= amount;
      extracted += amount;
    }
    return extracted;
  }

}
