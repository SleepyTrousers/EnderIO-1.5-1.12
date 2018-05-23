package crazypants.enderio.invpanel.client;

import java.text.Collator;
import java.util.Comparator;

class NameComparator implements Comparator<ItemEntry> {
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
