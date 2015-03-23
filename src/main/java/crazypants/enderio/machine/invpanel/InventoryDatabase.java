package crazypants.enderio.machine.invpanel;

import crazypants.enderio.conduit.item.NetworkedInventory;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;
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
  private static final int HAS_NBT_FLAG = 1 << 31;

  private final HashMap<Integer, ItemKey> simpleRegsitry;
  private final HashMap<ItemKey, ItemKey> complexRegistry;
  private final ArrayList<ItemKey> complexItems;

  private int generation;
  private InventoryKey[] inventories;
  private int currentInventory;

  public InventoryDatabase() {
    simpleRegsitry = new HashMap<Integer, ItemKey>();
    complexRegistry = new HashMap<ItemKey, ItemKey>();
    complexItems = new ArrayList<ItemKey>();
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
      InflaterInputStream inf = new InflaterInputStream(new ByteArrayInputStream(compressed));
      try {
        BufferedInputStream bis = new BufferedInputStream(inf);
        DataInputStream dis = new DataInputStream(inf);
        int srcGeneration = dis.readInt();
        if(srcGeneration != generation) {
          return;
        }
        int count = dis.readUnsignedShort();
        for(int i=0 ; i<count ; i++) {
          int dbID = dis.readInt();
          NBTTagCompound nbt = null;
          if((dbID & HAS_NBT_FLAG) == HAS_NBT_FLAG) {
            dbID &= ~HAS_NBT_FLAG;
            nbt = CompressedStreamTools.read(dis);
          }
          
          if(dbID >= COMPLEX_DBINDEX_START) {
            int dbIndex = dbID - COMPLEX_DBINDEX_START;
            int itemID = dis.readUnsignedShort();
            int meta = dis.readUnsignedShort();

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
        inf.close();
      }
  }

  public byte[] compressItemInfo(List<ItemKey> items) throws IOException{
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    DeflaterOutputStream def = new DeflaterOutputStream(baos);
    try {
      BufferedOutputStream bos = new BufferedOutputStream(def);
      DataOutputStream dos = new DataOutputStream(bos);
      int count = items.size();
      dos.writeInt(generation);
      dos.writeShort(count);
      for(int i=0 ; i<count ; i++) {
        ItemKey item = items.get(i);
        item.write(dos);
      }
      dos.flush();
    } finally {
      def.close();
    }
    return baos.toByteArray();
  }

  public byte[] compressItemList() throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    DeflaterOutputStream def = new DeflaterOutputStream(baos);
    try {
      BufferedOutputStream bos = new BufferedOutputStream(def);
      DataOutputStream dos = new DataOutputStream(bos);
      for(Map.Entry<Integer, ItemKey> entry : simpleRegsitry.entrySet()) {
        int count = entry.getValue().countItems(this);
        if(count > 0) {
          dos.writeInt(count);
          dos.writeShort(entry.getKey());
        }
      }
      dos.writeInt(0);
      for(ItemKey key : complexItems) {
        if(key != null) {
          int count = key.countItems(this);
          if(count > 0) {
            dos.writeInt(count);
            dos.writeInt(key.dbID);
          }
        }
      }
      dos.writeInt(0);
      dos.flush();
    } finally {
      def.close();
    }
    return baos.toByteArray();
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
  }

  public void scanNextInventory() {
    if(inventories == null) {
      return;
    }

    InventoryKey inv = inventories[currentInventory];
    if(inv.scanInventory(this, currentInventory)) {
      System.out.println("Changed!");
    }

    currentInventory = (currentInventory+1) % inventories.length;
  }

  public static final class ItemKey {
    public final int dbID;
    public final int hash;
    public final int itemID;
    public final int meta;
    public final NBTTagCompound nbt;
    private HashSet<Integer> slots;

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

    void write(DataOutput dos) throws IOException {
      if(nbt != null) {
        dos.writeInt(dbID | HAS_NBT_FLAG);
        CompressedStreamTools.write(nbt, dos);
        if(dbID >= COMPLEX_DBINDEX_START) {
          dos.writeShort(itemID);
          dos.writeShort(meta);
        }
      } else {
        dos.writeInt(dbID);
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

    boolean scanInventory(InventoryDatabase db, int niIndex) {
      ItemStack[] items = ni.getExtractableItemStacks();
      if(items == null) {
        if(slotItems.length != 0) {
          resize(0, niIndex);
        }
        return true;
      }

      boolean changed = false;
      int count = items.length;
      if(count != slotItems.length) {
        resize(count, niIndex);
        changed = true;
      }

      for(int slot=0; slot<count; slot++) {
        ItemStack stack = items[slot];
        if(stack == null || stack.stackSize <= 0) {
          if(slotItems[slot] != null) {
            slotItems[slot].removeSlot(niIndex, slot);
            slotItems[slot] = null;
            itemCounts[slot] = 0;
            changed = true;
          }
        } else {
          ItemKey current = slotItems[slot];
          ItemKey key = db.lookupItem(stack, current);
          if(key != current) {
            if(current != null) {
              current.removeSlot(niIndex, slot);
            }
            slotItems[slot] = key;
            itemCounts[slot] = stack.stackSize;
            key.addSlot(niIndex, slot);
            changed = true;
          } else if(itemCounts[slot] != stack.stackSize) {
            itemCounts[slot] = stack.stackSize;
            changed = true;
          }
        }
      }
      return changed;
    }

    private void resize(int count, int niIndex) {
      for(int slot=0; slot<slotItems.length; slot++) {
        ItemKey key = slotItems[slot];
        if(key != null) {
          key.removeSlot(niIndex, slot);
        }
      }

      slotItems = new ItemKey[count];
      itemCounts = new int[count];
    }
  }
}
