package crazypants.enderio.machine.invpanel;

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
import java.util.List;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IntHashMap;

public class InventoryDatabase {

  private static final int SIMPLE_ITEMID_BITS = 12;
  private static final int SIMPLE_META_BITS   = 4;
  private static final int SIMPLE_MAX_ITEMID  = 1<<SIMPLE_ITEMID_BITS;
  private static final int SIMPLE_MAX_META    = 1<<SIMPLE_META_BITS;
  private static final int SIMPLE_META_MASK   = SIMPLE_MAX_META - 1;

  private static final int COMPLEX_DBINDEX_START = 1<<(SIMPLE_ITEMID_BITS + SIMPLE_META_BITS);
  private static final int HAS_NBT_FLAG = 1 << 31;

  private final IntHashMap simpleRegsitry;
  private final HashMap<ItemKey, ItemKey> itemRegistry;
  private final ArrayList<ItemKey> complexItems;

  public InventoryDatabase() {
    simpleRegsitry = new IntHashMap();
    itemRegistry = new HashMap<ItemKey, ItemKey>();
    complexItems = new ArrayList<ItemKey>();
  }

  public ItemKey lookupItem(ItemStack stack) {
    if(stack == null || stack.getItem() == null) {
      return null;
    }

    int itemID = Item.getIdFromItem(stack.getItem());
    int meta = Items.bone.getDamage(stack); // hack
    NBTTagCompound nbt = stack.stackTagCompound;

    if(nbt == null && itemID < SIMPLE_MAX_ITEMID && meta < SIMPLE_MAX_META) {
      return getSimpleItem(itemID, meta);
    } else {
      return getComplexItem(itemID, meta, nbt);
    }
  }

  private ItemKey getComplexItem(int itemID, int meta, NBTTagCompound nbt) {
    int hash = computeComplexHash(itemID, meta, nbt);
    ItemKey key = new ItemKey(-1, hash, itemID, meta, nbt);
    key = itemRegistry.get(key);
    if(key == null) {
      if(nbt != null) {
        nbt = (NBTTagCompound)nbt.copy();
      }
      key = new ItemKey(COMPLEX_DBINDEX_START + complexItems.size(), hash, itemID, meta, nbt);
      complexItems.add(key);
      itemRegistry.put(key, key);
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
    int dbID = (itemID << SIMPLE_META_BITS) | meta;
    ItemKey key = (ItemKey) simpleRegsitry.lookup(dbID);
    if(key == null) {
      key = new ItemKey(dbID, dbID, itemID, meta, null);
      simpleRegsitry.addKey(dbID, key);
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
              itemRegistry.put(item, item);
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

  public static final class ItemKey {
    public final int dbID;
    public final int hash;
    public final int itemID;
    public final int meta;
    public final NBTTagCompound nbt;

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
        if(this.itemID != other.itemID || this.meta != other.meta) {
          return false;
        }
        return (this.nbt == other.nbt) || (this.nbt != null && this.nbt.equals(other.nbt));
      }
      return false;
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
  }
}
