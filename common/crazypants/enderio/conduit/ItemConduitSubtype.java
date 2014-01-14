package crazypants.enderio.conduit;

import crazypants.util.Lang;

public class ItemConduitSubtype {

  public final String unlocalisedName;

  public final String uiName;

  public final String iconKey;

  public ItemConduitSubtype(String unlocalisedName, String iconKey) {
    this.unlocalisedName = unlocalisedName;
    this.uiName = Lang.localize(unlocalisedName);
    this.iconKey = iconKey;
  }

}
