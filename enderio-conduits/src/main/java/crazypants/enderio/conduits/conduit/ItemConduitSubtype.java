
package crazypants.enderio.conduits.conduit;

public class ItemConduitSubtype {

  public final String unlocalisedName;

  public final String modelLocation;

  public ItemConduitSubtype(String baseName, String iconKey) {
    this.unlocalisedName = "enderio." + baseName;
    this.modelLocation = iconKey;
  }

}