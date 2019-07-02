package crazypants.enderio.base.config.recipes.xml;

import crazypants.enderio.base.config.recipes.InvalidRecipeConfigException;

public class Item extends ItemOptional {

  @Override
  public Object readResolve() throws InvalidRecipeConfigException {
    super.readResolve();
    if (nullItem) {
      throw new InvalidRecipeConfigException("Missing item name");
    }
    return this;
  }

}