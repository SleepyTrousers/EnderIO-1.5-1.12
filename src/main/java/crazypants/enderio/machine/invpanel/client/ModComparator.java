package crazypants.enderio.machine.invpanel.client;

import java.text.Collator;

class ModComparator extends NameComparator {

  ModComparator(Collator collator) {
    super(collator);
  }

  @Override
  public int compare(ItemEntry a, ItemEntry b) {
    String modIdA = a.getModId();
    String modIdB = b.getModId();
    int res = collator.compare(modIdA, modIdB);
    if (res == 0) {
      res = super.compare(a, b);
    }
    return res;
  }

}
