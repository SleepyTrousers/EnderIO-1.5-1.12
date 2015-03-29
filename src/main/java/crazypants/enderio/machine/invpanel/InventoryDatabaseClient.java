package crazypants.enderio.machine.invpanel;

import cpw.mods.fml.common.registry.GameRegistry;
import crazypants.enderio.network.CompressedDataInput;
import crazypants.enderio.network.PacketHandler;
import java.io.IOException;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;

public class InventoryDatabaseClient extends InventoryDatabase<InventoryDatabaseClient.ItemEntry> {

  private final TileInventoryPanel te;
  private final ArrayList<ItemEntry> clientItems;
  private final HashSet<Integer> requestedItems;

  private SortOrder order = SortOrder.NAME;
  private boolean invertSortOrder;
  private boolean needsSorting;
  private Collator collator;

  public InventoryDatabaseClient(TileInventoryPanel te) {
    this.te = te;
    clientItems = new ArrayList<ItemEntry>();
    requestedItems = new HashSet<Integer>();
  }

  public void readCompressedItems(byte[] compressed) throws IOException {
    CompressedDataInput cdi = new CompressedDataInput(compressed);
    try {
      int pktGeneration = cdi.readVariable();
      if(pktGeneration != generation) {
        return;
      }
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
      needsSorting = true;
    } finally {
      cdi.close();
    }
  }

  public void readCompressedItemList(byte[] compressed) throws IOException {
    CompressedDataInput cdi = new CompressedDataInput(compressed);
    try {
      int pktGeneration = cdi.readVariable();
      int changed = cdi.readVariable();

      List<Integer> missingItems = null;

      if(changed > 0) {
        if(pktGeneration != generation) {
          return;
        }

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
      } else {
        for(ItemEntry entry : clientItems) {
          entry.count = 0;
        }
        clientItems.clear();
        generation = pktGeneration;

        int count = cdi.readVariable();
        while(count > 0) {
          int dbID = cdi.readUnsignedShort();
          ItemEntry entry = getSimpleItem(dbID);
          entry.count = count;
          clientItems.add(entry);
          count = cdi.readVariable();
        }

        count = cdi.readVariable();
        int dbID = COMPLEX_DBINDEX_START;
        while(count > 0) {
          dbID += cdi.readVariable();
          ItemEntry entry = getItem(dbID);
          if(entry != null) {
            entry.count = count;
            clientItems.add(entry);
          } else {
            missingItems = addMissingItems(missingItems, dbID);
          }
          count = cdi.readVariable();
        }
      }

      if(missingItems != null) {
        PacketHandler.INSTANCE.sendToServer(new PacketRequestMissingItems(te, missingItems));
      }
      needsSorting = true;
    } finally {
      cdi.close();
    }
  }

  private void setItemCount(ItemEntry entry, int count) {
    if(entry.count == 0 && count > 0) {
      clientItems.add(entry);
    } else if(entry.count > 0 && count == 0) {
      clientItems.remove(entry);
    }
    entry.count = count;
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

  private Collator getCollator() {
    if(collator == null) {
      String langCode = Minecraft.getMinecraft().getLanguageManager().getCurrentLanguage().getLanguageCode();
      Locale locale = Locale.forLanguageTag(langCode);
      collator = Collator.getInstance(locale);
    }
    return collator;
  }

  public void setSortOrder(SortOrder order, boolean invert) {
    if(this.order != order || this.invertSortOrder != invert) {
      this.order = order;
      this.invertSortOrder = invert;
      this.needsSorting = true;
    }
  }

  public SortOrder getSortOrder() {
    return order;
  }

  public boolean isSortOrderInverted() {
    return invertSortOrder;
  }

  public boolean sortItems() {
    if(needsSorting) {
      Comparator<ItemEntry> cmp;
      switch (order) {
        case NAME: cmp = new NameComparator(getCollator()); break;
        case MOD:  cmp = new ModComparator(getCollator()); break;
        default:   cmp = CountComparator.INSTANCE;
      }
      if(invertSortOrder) {
        cmp = Collections.reverseOrder(cmp);
      }
      Collections.sort(clientItems, cmp);
      return true;
    }
    return false;
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
    String name;
    String modId;
    int count;

    public ItemEntry(int dbID, int hash, int itemID, int meta, NBTTagCompound nbt) {
      super(dbID, hash, itemID, meta, nbt);
    }

    public int getCount() {
      return count;
    }

    public Item getItem() {
      return Item.getItemById(itemID);
    }

    public ItemStack makeItemStack() {
      ItemStack stack = new ItemStack(getItem(), count, meta);
      stack.stackTagCompound = nbt;
      return stack;
    }

    public String getUnlocName() {
      if(name == null) {
        findUnlocName();
      }
      return name;
    }

    private void findUnlocName() {
      ItemStack stack = makeItemStack();
      try {
        name = stack.getDisplayName();
        if(name == null || name.isEmpty()) {
          name = stack.getItem().getUnlocalizedName();
          if(name == null || name.isEmpty()) {
            name = stack.getItem().getClass().getName();
          }
        }
      } catch(Throwable ex) {
        name = "Exception: " + ex.getMessage();
      }
    }

    public String getModId() {
      if(modId == null) {
        findModId();
      }
      return modId;
    }

    private void findModId() {
      Item item = getItem();
      GameRegistry.UniqueIdentifier id = GameRegistry.findUniqueIdentifierFor(item);
      if(id != null && id.modId != null) {
        modId = id.modId;
      } else {
        modId = "Unknown";
      }
    }
  }

  static class NameComparator implements Comparator<ItemEntry> {
    protected final Collator collator;

    NameComparator(Collator collator) {
      this.collator = collator;
    }

    @Override
    public int compare(ItemEntry a, ItemEntry b) {
      String nameA = a.getUnlocName();
      String nameB = b.getUnlocName();
      return collator.compare(nameA, nameB);
    }
  }

  static class ModComparator extends NameComparator {
    ModComparator(Collator collator) {
      super(collator);
    }

    @Override
    public int compare(ItemEntry a, ItemEntry b) {
      String modIdA = a.getModId();
      String modIdB = b.getModId();
      int res = collator.compare(modIdA, modIdB);
      if(res == 0) {
        res = super.compare(a, b);
      }
      return res;
    }
  }

  static class CountComparator implements Comparator<ItemEntry> {
    public static final CountComparator INSTANCE = new CountComparator();
    @Override
    public int compare(ItemEntry a, ItemEntry b) {
      return a.count - b.count;
    }
  }
}
