package crazypants.enderio.config.recipes.xml;

import crazypants.enderio.config.recipes.InvalidRecipeConfigException;

public class Item extends OptionalItem {

  @Override
  public Object readResolve() throws InvalidRecipeConfigException {
    super.readResolve();
    if (valid) {
      throw new InvalidRecipeConfigException("Missing items name");
    }
    return this;
  }

}