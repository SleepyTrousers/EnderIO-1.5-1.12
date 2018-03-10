package crazypants.enderio.machine.invpanel.server;

import java.util.IdentityHashMap;

import crazypants.enderio.conduits.conduit.item.IInventoryDatabaseServer;
import crazypants.enderio.conduits.conduit.item.IServerItemEntry;
import crazypants.enderio.conduits.conduit.item.ItemEntryBase;
import crazypants.enderio.conduits.conduit.item.SlotKey;
import net.minecraft.nbt.NBTTagCompound;

public class ItemEntry extends ItemEntryBase implements IServerItemEntry {
  private final IdentityHashMap<SlotKey, SlotKey> slots = new IdentityHashMap<SlotKey, SlotKey>();

  public ItemEntry(int dbID, int hash, int itemID, int meta, NBTTagCompound nbt) {
    super(dbID, hash, itemID, meta, nbt);
  }

  @Override
  public void addSlot(SlotKey slotKey) {
    slots.put(slotKey, slotKey);
  }

  @Override
  public void removeSlot(SlotKey slotKey) {
    slots.remove(slotKey);
  }

  @Override
  public int countItems() {
    int count = 0;
    for (SlotKey slotKey : slots.values()) {
      count += slotKey.count;
    }
    return count;
  }

  @Override
  public int extractItems(IInventoryDatabaseServer db, int count) {
    int extracted = 0;
    SlotKey[] copy = slots.values().toArray(new SlotKey[slots.size()]);
    for (SlotKey slotKey : copy) {
      int amount = slotKey.inv.extractItem(db, this, slotKey.slot, count);
      count -= amount;
      extracted += amount;
      if (count <= 0) {
        break;
      }
    }
    return extracted;
  }

}
