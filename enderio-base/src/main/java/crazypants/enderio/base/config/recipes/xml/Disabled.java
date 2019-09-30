package crazypants.enderio.base.config.recipes.xml;

import javax.annotation.Nonnull;

import crazypants.enderio.base.Log;
import crazypants.enderio.base.config.recipes.InvalidRecipeConfigException;

public class Disabled extends AbstractConditional {

  @Override
  public Object readResolve() throws InvalidRecipeConfigException {
    try {
      super.readResolve();
      valid = true;
    } catch (InvalidRecipeConfigException e) {
      throw new InvalidRecipeConfigException(e, "in <disabled>");
    }
    return this;
  }

  @Override
  public void enforceValidity() throws InvalidRecipeConfigException {
  }

  @Override
  public void register(@Nonnull String recipeName) {
    if (isValid() && isActive()) {
      Log.debug("Recipe '" + recipeName + "' is disabled");
    }
  }

}
