package crazypants.enderio.base.config.recipes;

import javax.annotation.Nonnull;

public interface IRecipeGameRecipe extends IRecipeConfigElement {

  boolean isActive();

  void register(@Nonnull String recipeName);

  default @Nonnull String getName() {
    return "unnamed";
  }

}