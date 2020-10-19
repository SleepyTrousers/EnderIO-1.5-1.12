package crazypants.enderio.base.config.recipes;

import javax.annotation.Nonnull;

import crazypants.enderio.base.recipe.RecipeLevel;

public interface IRecipeGameRecipe extends IRecipeConfigElement {

  boolean isActive();

  void register(@Nonnull String recipeName, @Nonnull RecipeLevel recipeLevel);

  default @Nonnull String getName() {
    return "unnamed";
  }

}