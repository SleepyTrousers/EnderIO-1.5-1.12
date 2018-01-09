
package crazypants.enderio.conduit;

public class ItemConduitSubtype {

  public final String baseName;

  public final String unlocalisedName;

  public final String modelLocation;

  public ItemConduitSubtype(String baseName, String iconKey) {
    this.baseName = baseName;
    this.unlocalisedName = "enderio." + baseName;
    this.modelLocation = iconKey;
  }

}