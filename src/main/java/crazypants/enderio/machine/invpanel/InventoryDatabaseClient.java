package crazypants.enderio.machine.invpanel;

import crazypants.enderio.network.CompressedDataInput;
import java.io.IOException;
import java.util.ArrayList;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;

public class InventoryDatabaseClient extends InventoryDatabase<InventoryDatabaseClient.ItemEntry> {

  private final ArrayList<ItemEntry> clientItems;
  
  private boolean clientListNeedsSorting;

  public InventoryDatabaseClient() {
    clientItems = new ArrayList<ItemEntry>();
  }

  public void readCompressedItems(byte[] compressed) throws IOException {
    CompressedDataInput cdi = new CompressedDataInput(compressed);
    try {
      int pktGeneration = cdi.readVariable();
      if(pktGeneration != generation) {
        return;
      }
      int count = cdi.readVariable();
      for(int i=0 ; i<count ; i++) {
        int code = cdi.readVariable();
        int dbID = code >> 1;
        NBTTagCompound nbt = null;
        if((code & 1) == 1) {
          nbt = CompressedStreamTools.read(cdi);
        }

        if(dbID >= COMPLEX_DBINDEX_START) {
          int dbIndex = dbID - COMPLEX_DBINDEX_START;
          int itemID = cdi.readVariable();
          int meta = cdi.readVariable();

          while(complexItems.size() <= dbIndex) {
            complexItems.add(null);
          }

          ItemEntry entry = complexItems.get(dbIndex);
          if(entry == null) {
            entry = createItemEntry(dbID, itemID, meta, nbt);
            complexItems.set(dbIndex, entry);
            complexRegistry.put(entry, entry);
          }
        } else {
          getSimpleItem(dbID);
        }
      }
    } finally {
      cdi.close();
    }
  }

  public void readCompressedItemList(byte[] compressed) throws IOException {
    CompressedDataInput cdi = new CompressedDataInput(compressed);
    try {
      int pktGeneration = cdi.readVariable();
      int changed = cdi.readVariable();

      if(changed > 0) {
        if(pktGeneration != generation) {
          return;
        }

        for(int i = 0; i < changed; i++) {
          int dbID = cdi.readVariable();
          int count = cdi.readVariable();
          ItemEntry entry = getItem(dbID);
          if(entry != null) {
            if(entry.clientCount == 0 && count > 0) {
              clientItems.add(entry);
            } else if(entry.clientCount > 0 && count == 0) {
              clientItems.remove(entry);
            }
            entry.clientCount = count;
          }
        }
      } else {
        for(ItemEntry entry : clientItems) {
          entry.clientCount = 0;
        }
        clientItems.clear();
        generation = pktGeneration;

        int count = cdi.readVariable();
        while(count > 0) {
          int dbID = cdi.readUnsignedShort();
          ItemEntry entry = getSimpleItem(dbID);
          entry.clientCount = count;
          clientItems.add(entry);
          count = cdi.readVariable();
        }

        count = cdi.readVariable();
        int dbID = COMPLEX_DBINDEX_START;
        while(count > 0) {
          dbID += cdi.readVariable();
          ItemEntry entry = getItem(dbID);
          if(entry != null) {
            entry.clientCount = count;
            clientItems.add(entry);
          }
          count = cdi.readVariable();
        }
      }

      clientListNeedsSorting = true;
    } finally {
      cdi.close();
    }
  }

  public boolean sortClientItems() {
    boolean res = clientListNeedsSorting;
    clientListNeedsSorting = false;
    return res;
  }

  public int getNumEntries() {
    return clientItems.size();
  }

  public ItemEntry getItemEntry(int index) {
    return clientItems.get(index);
  }

  public ItemStack getItemStack(int index) {
    return getItemEntry(index).makeItemStack();
  }

  @Override
  protected ItemEntry createItemEntry(int dbId, int hash, int itemID, int meta, NBTTagCompound nbt) {
    return new ItemEntry(dbId, hash, itemID, meta, nbt);
  }

  public static class ItemEntry extends ItemEntryBase {
    int clientCount;

    public ItemEntry(int dbID, int hash, int itemID, int meta, NBTTagCompound nbt) {
      super(dbID, hash, itemID, meta, nbt);
    }

    public int getClientCount() {
      return clientCount;
    }

    public ItemStack makeItemStack() {
      ItemStack stack = new ItemStack(Item.getItemById(itemID), clientCount, meta);
      stack.stackTagCompound = nbt;
      return stack;
    }
  }
}
