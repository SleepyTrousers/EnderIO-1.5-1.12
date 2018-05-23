package crazypants.enderio.invpanel.client;

import com.enderio.core.common.network.CompressedDataInput;

import crazypants.enderio.invpanel.database.InventoryDatabase;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class InventoryDatabaseClient extends InventoryDatabase<ItemEntry> {

  private final ArrayList<ItemEntry> clientItems;
  private final HashSet<Integer> requestedItems;

  private int itemsChangeCount;
  private int countChangeCount;

  InventoryDatabaseClient(int generation) {
    this.generation = generation;
    clientItems = new ArrayList<ItemEntry>();
    requestedItems = new HashSet<Integer>();
  }

  public int getItemsChangeCount() {
    return itemsChangeCount;
  }

  public int getCountChangeCount() {
    return countChangeCount;
  }

  public void getItems(List<ItemEntry> outList) {
    outList.addAll(clientItems);
  }

  public void readCompressedItems(byte[] compressed) throws IOException {
    CompressedDataInput cdi = new CompressedDataInput(compressed);
    try {
      int numEntries = cdi.readVariable();
      for(int i=0 ; i<numEntries ; i++) {
        int code = cdi.readVariable();
        int itemID = cdi.readVariable();
        int meta = cdi.readVariable();
        NBTTagCompound nbt = null;

        int dbIndex = code >> 1;
        if((code & 1) == 1) {
          nbt = CompressedStreamTools.read(cdi);
        }

        // item order can vary, ensure that the slot exists
        complexItems.ensureCapacity(dbIndex + 1);
        while(complexItems.size() <= dbIndex) {
          complexItems.add(null);
        }

        ItemEntry entry = complexItems.get(dbIndex);
        if(entry == null) {
          entry = createItemEntry(dbIndex + COMPLEX_DBINDEX_START, itemID, meta, nbt);
          complexItems.set(dbIndex, entry);
          complexRegistry.put(entry, entry);
        }

        int count = cdi.readVariable();
        setItemCount(entry, count);
      }
      itemsChangeCount++;
      countChangeCount++;
    } finally {
      cdi.close();
    }
  }

  public List<Integer> readCompressedItemList(byte[] compressed) throws IOException {
    CompressedDataInput cdi = new CompressedDataInput(compressed);
    try {
      List<Integer> missingItems = null;
      int changed = cdi.readVariable();
      if(changed > 0) {
        for(int i = 0; i < changed; i++) {
          int dbID = cdi.readVariable();
          int count = cdi.readVariable();
          ItemEntry entry = getItem(dbID);
          if(entry != null) {
            setItemCount(entry, count);
          } else {
            missingItems = addMissingItems(missingItems, dbID);
          }
        }

        countChangeCount++;
      } else {
        for(ItemEntry entry : clientItems) {
          entry.setCount(0);
        }
        clientItems.clear();
        requestedItems.clear();

        int count = cdi.readVariable();
        while(count > 0) {
          int dbID = cdi.readUnsignedShort();
          ItemEntry entry = getSimpleItem(dbID);
          entry.setCount(count);
          clientItems.add(entry);
          count = cdi.readVariable();
        }

        count = cdi.readVariable();
        int dbID = COMPLEX_DBINDEX_START;
        while(count > 0) {
          dbID += cdi.readVariable();
          ItemEntry entry = getItem(dbID);
          if(entry != null) {
            entry.setCount(count);
            clientItems.add(entry);
          } else {
            missingItems = addMissingItems(missingItems, dbID);
          }
          count = cdi.readVariable();
        }
        itemsChangeCount++;
        countChangeCount++;
      }

      return missingItems;
    } finally {
      cdi.close();
    }
  }

  private void setItemCount(ItemEntry entry, int count) {
    if(entry.getCount() == 0 && count > 0) {
      clientItems.add(entry);
      itemsChangeCount++;
    } else if(entry.getCount() > 0 && count == 0) {
      clientItems.remove(entry);
      itemsChangeCount++;
    }
    entry.setCount(count);
  }

  private List<Integer> addMissingItems(List<Integer> list, Integer dbId) {
    if(!requestedItems.contains(dbId)) {
      if(list == null) {
        list = new ArrayList<Integer>();
      }
      list.add(dbId);
      requestedItems.add(dbId);
    }
    return list;
  }

  @Override
  protected ItemEntry createItemEntry(int dbId, int hash, int itemID, int meta, NBTTagCompound nbt) {
    return new ItemEntry(dbId, hash, itemID, meta, nbt);
  }

}
