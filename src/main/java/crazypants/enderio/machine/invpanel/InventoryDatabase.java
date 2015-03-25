package crazypants.enderio.machine.invpanel;

import crazypants.enderio.conduit.item.NetworkedInventory;
import crazypants.enderio.network.CompressedDataInput;
import crazypants.enderio.network.CompressedDataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;

public class InventoryDatabase {

  private static final int SIMPLE_ITEMID_BITS = 12;
  private static final int SIMPLE_META_BITS   = 4;
  private static final int SIMPLE_MAX_ITEMID  = 1<<SIMPLE_ITEMID_BITS;
  private static final int SIMPLE_MAX_META    = 1<<SIMPLE_META_BITS;
  private static final int SIMPLE_META_MASK   = SIMPLE_MAX_META - 1;

  private static final int COMPLEX_DBINDEX_START = 1<<(SIMPLE_ITEMID_BITS + SIMPLE_META_BITS);
  
  private final HashMap<Integer, ItemKey> simpleRegsitry;
  private final HashMap<ItemKey, ItemKey> complexRegistry;
  private final ArrayList<ItemKey> complexItems;
  private final ArrayList<ItemKey> clientItems;

  private int generation;
  private InventoryKey[] inventories;
  private int currentInventory;
  private ChangeLog[] changeLogs;

  private boolean clientListNeedsSorting;

  public InventoryDatabase() {
    simpleRegsitry = new HashMap<Integer, ItemKey>();
    complexRegistry = new HashMap<ItemKey, ItemKey>();
    complexItems = new ArrayList<ItemKey>();
    clientItems = new ArrayList<ItemKey>();
    changeLogs = new ChangeLog[0];
  }

  public void addChangeLog(ChangeLog log) {
    for(ChangeLog alog : changeLogs) {
      if(alog == log) {
        return;
      }
    }
    int count = changeLogs.length;
    changeLogs = Arrays.copyOf(changeLogs, count+1);
    changeLogs[count] = log;
  }

  public void removeChangeLog(ChangeLog log) {
    int count = changeLogs.length;
    for(int i = 0; i < count ; i++) {
      if(changeLogs[i] == log) {
        ChangeLog[] tmp = new ChangeLog[--count];
        System.arraycopy(changeLogs, 0, tmp, 0, i);
        System.arraycopy(changeLogs, i+1, tmp, i, count-i);
        changeLogs = tmp;
        break;
      }
    }
  }

  public ItemKey lookupItem(ItemStack stack, ItemKey hint) {
    if(stack == null || stack.getItem() == null) {
      return null;
    }

    int itemID = Item.getIdFromItem(stack.getItem());
    int meta = Items.bone.getDamage(stack); // hack
    NBTTagCompound nbt = stack.stackTagCompound;

    if(nbt != null && nbt.hasNoTags()) {
      nbt = null;
    }

    if(hint != null && hint.equals(itemID, meta, nbt)) {
      return hint;
    }

    if(nbt == null && itemID < SIMPLE_MAX_ITEMID && meta < SIMPLE_MAX_META) {
      return getSimpleItem(itemID, meta);
    } else {
      return getComplexItem(itemID, meta, nbt);
    }
  }

  private ItemKey getComplexItem(int itemID, int meta, NBTTagCompound nbt) {
    int hash = computeComplexHash(itemID, meta, nbt);
    ItemKey key = new ItemKey(-1, hash, itemID, meta, nbt);
    key = complexRegistry.get(key);
    if(key == null) {
      if(nbt != null) {
        nbt = (NBTTagCompound)nbt.copy();
      }
      key = new ItemKey(COMPLEX_DBINDEX_START + complexItems.size(), hash, itemID, meta, nbt);
      complexItems.add(key);
      complexRegistry.put(key, key);
      System.out.println("New complex item: " + key);
    }

    return key;
  }

  private static int computeComplexHash(int itemID, int meta, NBTTagCompound nbt) {
    int hash = ((itemID * 257) ^ meta) * 17;
    if(nbt != null) {
      hash ^= nbt.hashCode();
    }
    return hash;
  }

  private ItemKey getSimpleItem(int itemID, int meta) {
    Integer dbID = (itemID << SIMPLE_META_BITS) | meta;
    ItemKey key = (ItemKey) simpleRegsitry.get(dbID);
    if(key == null) {
      key = new ItemKey(dbID, dbID, itemID, meta, null);
      simpleRegsitry.put(dbID, key);
      System.out.println("New simple item: " + key);
    }
    return key;
  }

  private ItemKey getSimpleItem(int dbID) {
    int itemID = dbID >> SIMPLE_META_BITS;
    int meta = dbID & SIMPLE_META_MASK;
    return getSimpleItem(itemID, meta);
  }

  public ItemKey getItem(int dbID) {
    if(dbID < COMPLEX_DBINDEX_START) {
      return getSimpleItem(dbID);
    }

    int dbIndex = dbID - COMPLEX_DBINDEX_START;
    if(dbIndex < complexItems.size()) {
      return complexItems.get(dbIndex);
    }
    return null;
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

          ItemKey item = complexItems.get(dbIndex);
          if(item == null) {
            int hash = computeComplexHash(itemID, meta, nbt);
            item = new ItemKey(dbID, hash, itemID, meta, nbt);
            complexItems.set(dbIndex, item);
            complexRegistry.put(item, item);
          }
        } else {
          getSimpleItem(dbID);
        }
      }
    } finally {
      cdi.close();
    }
  }

  public byte[] compressItemInfo(List<ItemKey> items) throws IOException{
    CompressedDataOutput cdo = new CompressedDataOutput();
    try {
      int count = items.size();
      cdo.writeVariable(generation);
      cdo.writeVariable(count);
      for(int i=0 ; i<count ; i++) {
        ItemKey item = items.get(i);
        item.write(cdo);
      }
      return cdo.getCompressed();
    } finally {
      cdo.close();
    }
  }

  public byte[] compressItemList() throws IOException {
    CompressedDataOutput cdo = new CompressedDataOutput();
    try {
      cdo.writeVariable(generation);
      cdo.writeByte(0);
      for(Map.Entry<Integer, ItemKey> entry : simpleRegsitry.entrySet()) {
        int count = entry.getValue().countItems(this);
        if(count > 0) {
          cdo.writeVariable(count);
          cdo.writeShort(entry.getKey());
        }
      }
      cdo.writeByte(0);
      int prevID = COMPLEX_DBINDEX_START;
      for(ItemKey key : complexItems) {
        if(key != null) {
          int count = key.countItems(this);
          if(count > 0) {
            cdo.writeVariable(count);
            cdo.writeVariable(key.dbID - prevID);
            prevID = key.dbID;
          }
        }
      }
      cdo.writeByte(0);
      return cdo.getCompressed();
    } finally {
      cdo.close();
    }
  }

  public byte[] compressChangedItems(Collection<ItemKey> items) throws IOException {
    CompressedDataOutput cdo = new CompressedDataOutput();
    try {
      cdo.writeVariable(generation);
      cdo.writeVariable(items.size());
      for(ItemKey key : items) {
        cdo.writeVariable(key.dbID);
        cdo.writeVariable(key.countItems(this));
      }
      return cdo.getCompressed();
    } finally {
      cdo.close();
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
          ItemKey key = getItem(dbID);
          if(key != null) {
            if(key.clientCount == 0 && count > 0) {
              clientItems.add(key);
            } else if(key.clientCount > 0 && count == 0) {
              clientItems.remove(key);
            }
            key.clientCount = count;
          }
        }
      } else {
        clientItems.clear();
        for(ItemKey key : simpleRegsitry.values()) {
          key.clientCount = 0;
        }
        for(ItemKey key : complexItems) {
          key.clientCount = 0;
        }
        generation = pktGeneration;
        
        int count = cdi.readVariable();
        while(count > 0) {
          int dbID = cdi.readUnsignedShort();
          ItemKey key = getSimpleItem(dbID);
          key.clientCount = count;
          clientItems.add(key);
          count = cdi.readVariable();
        }

        count = cdi.readVariable();
        int dbID = COMPLEX_DBINDEX_START;
        while(count > 0) {
          dbID += cdi.readVariable();
          ItemKey key = getItem(dbID);
          if(key != null) {
            key.clientCount = count;
            clientItems.add(key);
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

  public int getClientItemCount() {
    return clientItems.size();
  }

  public ItemKey getClientItem(int index) {
    return clientItems.get(index);
  }

  public void setNetworkSources(List<NetworkedInventory> sources) {
    simpleRegsitry.clear();
    complexRegistry.clear();
    complexItems.clear();
    generation++;
    currentInventory = 0;

    if(sources == null || sources.isEmpty()) {
      this.inventories = null;
    } else {
      this.inventories = new InventoryKey[sources.size()];
      for(int i=0; i<sources.size(); i++) {
        inventories[i] = new InventoryKey(sources.get(i));
      }
    }

    for(ChangeLog log : changeLogs) {
      log.databaseReset();
    }
  }

  public void scanNextInventory() {
    if(inventories == null) {
      return;
    }

    InventoryKey inv = inventories[currentInventory];
    inv.scanInventory(this, currentInventory);

    currentInventory = (currentInventory+1) % inventories.length;
  }

  public void sendChangeLogs() {
    for(ChangeLog log : changeLogs) {
      log.sendChangeLog();
    }
  }

  void itemChanged(ItemKey key) {
    for(ChangeLog log : changeLogs) {
      log.itemChanged(key);
    }
  }

  public static final class ItemKey {
    public final int dbID;
    public final int hash;
    public final int itemID;
    public final int meta;
    public final NBTTagCompound nbt;
    private HashSet<Integer> slots;

    int clientCount;

    ItemKey(int dbID, int hash, int itemID, int meta, NBTTagCompound nbt) {
      this.dbID = dbID;
      this.hash = hash;
      this.itemID = itemID;
      this.meta = meta;
      this.nbt = nbt;
    }

    @Override
    public int hashCode() {
      return hash;
    }

    @Override
    public boolean equals(Object obj) {
      if(obj instanceof ItemKey) {
        final ItemKey other = (ItemKey) obj;
        return equals(other.itemID, other.meta, other.nbt);
      }
      return false;
    }

    boolean equals(int itemID, int meta, NBTTagCompound nbt) {
      return this.itemID == itemID && this.meta == meta &&
              (this.nbt == nbt) || (this.nbt != null && this.nbt.equals(nbt));
    }

    @Override
    public String toString() {
      return "ItemKey{" + "dbID=" + dbID + ", hash=" + hash + ", itemID=" + itemID + ", meta=" + meta + ", nbt=" + nbt + '}';
    }

    public int getClientCount() {
      return clientCount;
    }

    public ItemStack makeItemStack() {
      ItemStack stack = new ItemStack(Item.getItemById(itemID), clientCount, meta);
      stack.stackTagCompound = nbt;
      return stack;
    }

    void write(CompressedDataOutput cdo) throws IOException {
      if(nbt != null) {
        cdo.writeInt((dbID << 1) | 1);
        CompressedStreamTools.write(nbt, cdo);
        if(dbID >= COMPLEX_DBINDEX_START) {
          cdo.writeVariable(itemID);
          cdo.writeVariable(meta);
        }
      } else {
        cdo.writeVariable(dbID << 1);
      }
    }

    static int encodeNISlot(int niIndex, int slot) {
      return (niIndex << 12) | slot;
    }

    void addSlot(int niIndex, int slot) {
      if(slots == null) {
        slots = new HashSet<Integer>();
      }
      slots.add(encodeNISlot(niIndex, slot));
    }

    void removeSlot(int niIndex, int slot) {
      if(slots != null) {
        slots.remove(encodeNISlot(niIndex, slot));
      }
    }

    int countItems(InventoryDatabase db) {
      int count = 0;
      if(slots != null) {
        for(Integer idx : slots) {
          InventoryKey key = db.inventories[idx >> 12];
          count += key.itemCounts[idx & 4095];
        }
      }
      return count;
    }
  }

  static class InventoryKey {
    private static final ItemKey[] NO_SLOTS = new ItemKey[0];

    final NetworkedInventory ni;
    ItemKey[] slotItems = NO_SLOTS;
    int[] itemCounts;

    InventoryKey(NetworkedInventory ni) {
      this.ni = ni;
    }

    void scanInventory(InventoryDatabase db, int niIndex) {
      ItemStack[] items = ni.getExtractableItemStacks();
      if(items == null) {
        if(slotItems.length != 0) {
          resize(db, 0, niIndex);
        }
        return;
      }

      int count = items.length;
      if(count != slotItems.length) {
        resize(db, count, niIndex);
      }

      for(int slot=0; slot<count; slot++) {
        ItemStack stack = items[slot];
        ItemKey current = slotItems[slot];
        if(stack == null || stack.stackSize <= 0) {
          if(current != null) {
            slotItems[slot] = null;
            itemCounts[slot] = 0;
            current.removeSlot(niIndex, slot);
            db.itemChanged(current);
          }
        } else {
          ItemKey key = db.lookupItem(stack, current);
          if(key != current) {
            slotItems[slot] = key;
            itemCounts[slot] = stack.stackSize;
            key.addSlot(niIndex, slot);
            db.itemChanged(key);
            if(current != null) {
              current.removeSlot(niIndex, slot);
              db.itemChanged(current);
            }
          } else if(itemCounts[slot] != stack.stackSize) {
            itemCounts[slot] = stack.stackSize;
            db.itemChanged(current);
          }
        }
      }
    }

    private void resize(InventoryDatabase db, int count, int niIndex) {
      for(int slot=0; slot<slotItems.length; slot++) {
        ItemKey key = slotItems[slot];
        if(key != null) {
          key.removeSlot(niIndex, slot);
          db.itemChanged(key);
        }
      }

      slotItems = new ItemKey[count];
      itemCounts = new int[count];
    }
  }

  public interface ChangeLog {
    void itemChanged(ItemKey key);
    void databaseReset();
    void sendChangeLog();
  }
}
