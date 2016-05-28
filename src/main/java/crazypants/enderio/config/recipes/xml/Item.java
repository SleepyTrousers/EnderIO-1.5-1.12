package crazypants.enderio.config.recipes.xml;

import crazypants.enderio.Log;
import crazypants.enderio.config.recipes.InvalidRecipeConfigException;

public class Item extends OptionalItem {

  @Override
  public Object readResolve() throws InvalidRecipeConfigException {
    super.readResolve();
    if (nullItem) {
      throw new InvalidRecipeConfigException("Missing items name");
    }
    return this;
  }

  @Override
  public boolean isValid() {
    if (!super.isValid()) {
      Log.info("Could not find a crafting ingredient for '" + name + "' (stack=" + stack + ", object=" + recipeObject + ")");
      return false;
    }
    return true;
  }

}