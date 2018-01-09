package crazypants.enderio.conduit;

import crazypants.enderio.base.init.IModObject;

import javax.annotation.Nonnull;

public class ItemConduitSubtype {
  
  public final String unlocalisedName;

  public final String modelLocation;

  public ItemConduitSubtype(@Nonnull IModObject modObject, String iconKey) {
    this.unlocalisedName = modObject.getUnlocalisedName();
    this.modelLocation = iconKey;
  }

}
