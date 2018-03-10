package crazypants.enderio.machine.invpanel;

import java.util.ArrayList;
import java.util.HashMap;

import crazypants.enderio.conduits.conduit.item.IInventoryDatabase;
import crazypants.enderio.conduits.conduit.item.IItemEntry;
import crazypants.enderio.conduits.conduit.item.ItemEntryBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public abstract class InventoryDatabase<ItemEntry extends IItemEntry> implements IInventoryDatabase<ItemEntry> {

  private static final Item META_EXTRACTOR = new Item();

  private static final int SIMPLE_ITEMID_BITS = 12;
  private static final int SIMPLE_META_BITS = 4;
  private static final int SIMPLE_MAX_ITEMID = 1 << SIMPLE_ITEMID_BITS;
  private static final int SIMPLE_MAX_META = 1 << SIMPLE_META_BITS;
  private static final int SIMPLE_META_MASK = SIMPLE_MAX_META - 1;

  protected static final int COMPLEX_DBINDEX_START = 1 << (SIMPLE_ITEMID_BITS + SIMPLE_META_BITS);

  protected final HashMap<Integer, ItemEntry> simpleRegsitry;
  protected final HashMap<ItemEntry, ItemEntry> complexRegistry;
  protected final ArrayList<ItemEntry> complexItems;

  protected int generation;

  public InventoryDatabase() {
    simpleRegsitry = new HashMap<Integer, ItemEntry>();
    complexRegistry = new HashMap<ItemEntry, ItemEntry>();
    complexItems = new ArrayList<ItemEntry>();
  }
  
  @Override
  public int getGeneration() {
    return generation;
  }

  @Override
  public ItemEntry lookupItem(ItemStack stack, ItemEntry hint, boolean create) {
    if(stack == null || stack.getItem() == null) {
      return null;
    }
    
    int itemID = Item.getIdFromItem(stack.getItem());
    int meta = META_EXTRACTOR.getDamage(stack);
    NBTTagCompound nbt = stack.getTagCompound();

    if(nbt != null && nbt.hasNoTags()) {
      nbt = null;
    }

    if(hint != null && hint.equals(itemID, meta, nbt)) {
      return hint;
    }

    if(nbt == null && itemID < SIMPLE_MAX_ITEMID && meta < SIMPLE_MAX_META) {
      return getSimpleItem(itemID, meta, create);
    } else {
      return getComplexItem(itemID, meta, nbt, create);
    }
  }

  protected abstract ItemEntry createItemEntry(int dbId, int hash, int itemID, int meta, NBTTagCompound nbt);

  protected ItemEntry createItemEntry(int dbId, int itemID, int meta, NBTTagCompound nbt) {
    int hash = computeComplexHash(itemID, meta, nbt);
    return createItemEntry(dbId, hash, itemID, meta, nbt);
  }

  private ItemEntry getComplexItem(int itemID, int meta, NBTTagCompound nbt, boolean create) {
    int hash = computeComplexHash(itemID, meta, nbt);
    ItemEntryKey key = new ItemEntryKey(hash, itemID, meta, nbt);
    ItemEntry entry = complexRegistry.get(key);
    if(entry == null && create) {
      if(nbt != null) {
        nbt = nbt.copy();
      }
      entry = createItemEntry(COMPLEX_DBINDEX_START + complexItems.size(), hash, itemID, meta, nbt);
      complexItems.add(entry);
      complexRegistry.put(entry, entry);
    }
    return entry;
  }

  private static int computeComplexHash(int itemID, int meta, NBTTagCompound nbt) {
    int hash = ((itemID * 257) ^ meta) * 17;
    if(nbt != null) {
      hash ^= nbt.hashCode();
    }
    return hash;
  }

  private ItemEntry getSimpleItem(int itemID, int meta, boolean create) {
    Integer dbID = (itemID << SIMPLE_META_BITS) | meta;
    ItemEntry entry = simpleRegsitry.get(dbID);
    if(entry == null && create) {
      entry = createItemEntry(dbID, dbID, itemID, meta, null);
      simpleRegsitry.put(dbID, entry);
    }
    return entry;
  }

  protected ItemEntry getSimpleItem(int dbID) {
    int itemID = dbID >> SIMPLE_META_BITS;
    int meta = dbID & SIMPLE_META_MASK;
    return getSimpleItem(itemID, meta, true);
  }

  @Override
  public ItemEntry getItem(int dbID) {
    if(dbID < COMPLEX_DBINDEX_START) {
      return getSimpleItem(dbID);
    }

    int dbIndex = dbID - COMPLEX_DBINDEX_START;
    if(dbIndex < complexItems.size()) {
      return complexItems.get(dbIndex);
    }
    return null;
  }

  @Override
  public ItemEntry getExistingItem(int dbID) {
    if(dbID < COMPLEX_DBINDEX_START) {
      return simpleRegsitry.get(dbID);
    }
    return getItem(dbID);
  }

  static final class ItemEntryKey {
    public final int hash;
    public final int itemID;
    public final int meta;
    public final NBTTagCompound nbt;

    ItemEntryKey(int hash, int itemID, int meta, NBTTagCompound nbt) {
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
      if(obj instanceof IItemEntry) {
        final ItemEntryBase other = (ItemEntryBase) obj;
        return other.equals(itemID, meta, nbt);
      }
      return false;
    }
  }
}
