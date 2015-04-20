package crazypants.enderio.machine.invpanel.client;

import java.util.Comparator;

class CountComparator implements Comparator<ItemEntry> {
  public static final CountComparator INSTANCE = new CountComparator();

  @Override
  public int compare(ItemEntry a, ItemEntry b) {
    return b.count - a.count;
  }

}
