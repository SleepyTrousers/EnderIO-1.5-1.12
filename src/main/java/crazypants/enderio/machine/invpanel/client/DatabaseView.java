package crazypants.enderio.machine.invpanel.client;

import crazypants.enderio.conduit.item.filter.IItemFilter;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Locale;
import net.minecraft.client.Minecraft;

public class DatabaseView {

  public static final Locale LOCALE;

  static {
    String languageCode = Minecraft.getMinecraft().getLanguageManager().getCurrentLanguage().getLanguageCode();
    int idx = languageCode.indexOf('_');
    if(idx > 0) {
      String lang = languageCode.substring(0, idx);
      String country = languageCode.substring(idx+1);
      LOCALE = new Locale(lang, country);
    } else {
      LOCALE = new Locale(languageCode);
    }
  }

  private final ArrayList<ItemEntry> filteredItems;

  private InventoryDatabaseClient database;
  private int dbItemsChangeCount;
  private int dbCountChangeCount;

  private SortOrder order = SortOrder.NAME;
  private boolean invertSortOrder;
  private boolean needsSorting;
  private IItemFilter itemFilter;
  private String currentFilter;
  private boolean needsFiltering;
  private boolean needsNewFiltering;

  private final Collator collator;

  public DatabaseView() {
    filteredItems = new ArrayList<ItemEntry>();
    currentFilter = "";
    collator = Collator.getInstance(LOCALE);
  }

  public void setDatabase(InventoryDatabaseClient database) {
    if(this.database != database) {
      this.database = database;
      if(database != null) {
        this.dbItemsChangeCount = database.getItemsChangeCount();
        this.dbCountChangeCount = database.getCountChangeCount();
      }
      this.needsSorting = true;
      this.needsFiltering = true;
      this.needsNewFiltering = true;
    }
  }

  public void setSortOrder(SortOrder order, boolean invert) {
    if(this.order != order || this.invertSortOrder != invert) {
      this.order = order;
      this.invertSortOrder = invert;
      this.needsSorting = true;
    }
  }

  public void setItemFilter(IItemFilter itemFilter) {
    if(this.itemFilter != itemFilter) {
      this.itemFilter = itemFilter;
      needsNewFiltering = true;
      needsFiltering = true;
    }
  }

  public void updateFilter(String newFilter) {
    newFilter = newFilter.trim();

    if(!currentFilter.equals(newFilter)) {
      if(newFilter.length() < currentFilter.length() ||
              !newFilter.regionMatches(0, currentFilter, 0, currentFilter.length())) {
        needsNewFiltering = true;
      }
      needsFiltering = true;
      currentFilter = newFilter;
    }
  }

  public SortOrder getSortOrder() {
    return order;
  }

  public boolean isSortOrderInverted() {
    return invertSortOrder;
  }

  public boolean sortItems() {
    boolean changed = false;

    if(database != null) {
      if(dbItemsChangeCount != database.getItemsChangeCount()) {
        dbItemsChangeCount = database.getItemsChangeCount();
        needsSorting = true;
        needsFiltering = true;
        needsNewFiltering = true;
      }

      if(dbCountChangeCount != database.getCountChangeCount()) {
        dbCountChangeCount = database.getCountChangeCount();
        if(order == SortOrder.COUNT) {
          needsSorting = true;
        }
      }
    }

    if(needsFiltering) {
      if(needsNewFiltering) {
        filteredItems.clear();
        if(database != null) {
          database.getItems(filteredItems);
        }
        needsSorting = true;
      }

      ItemFilter filter = ItemFilter.parse(currentFilter, LOCALE, itemFilter);
      if(filter != null) {
        Iterator<ItemEntry> iter = filteredItems.iterator();
        while(iter.hasNext()) {
          ItemEntry entry = iter.next();
          if(!filter.matches(entry)) {
            iter.remove();
            changed = true;
          }
        }
      }

      needsFiltering = false;
      needsNewFiltering = false;
    }

    if(needsSorting) {
      Comparator<ItemEntry> cmp;
      switch (order) {
        case NAME: cmp = new NameComparator(collator); break;
        case MOD:  cmp = new ModComparator(collator); break;
        default:   cmp = new CountComparator(collator); break;
      }
      if(invertSortOrder) {
        cmp = Collections.reverseOrder(cmp);
      }
      Collections.sort(filteredItems, cmp);
      changed = true;
      needsSorting = false;
    }

    return changed;
  }

  public int getNumEntries() {
    return filteredItems.size();
  }

  public ItemEntry getItemEntry(int index) {
    return filteredItems.get(index);
  }

}
