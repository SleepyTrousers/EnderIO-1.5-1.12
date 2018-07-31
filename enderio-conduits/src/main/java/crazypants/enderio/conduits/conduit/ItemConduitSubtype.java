
package crazypants.enderio.conduits.conduit;

import javax.annotation.Nonnull;

public class ItemConduitSubtype {

  final private @Nonnull String unlocalisedName;

  final private @Nonnull String modelLocation;

  public ItemConduitSubtype(@Nonnull String baseName, @Nonnull String iconKey) {
    this.unlocalisedName = "enderio." + baseName;
    this.modelLocation = iconKey;
  }

  public @Nonnull String getUnlocalisedName() {
    return unlocalisedName;
  }

  public @Nonnull String getModelLocation() {
    return modelLocation;
  }

}