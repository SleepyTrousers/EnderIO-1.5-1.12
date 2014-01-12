package crazypants.enderio.conduit;

import crazypants.enderio.EnderIO;

public class ItemConduitSubtype {

  public final String unlocalisedName;

  public final String uiName;

  public final String iconKey;

  public ItemConduitSubtype(String unlocalisedName, String iconKey) {
    this.unlocalisedName = unlocalisedName;
    this.uiName = EnderIO.localize(unlocalisedName);
    this.iconKey = iconKey;
  }

}
