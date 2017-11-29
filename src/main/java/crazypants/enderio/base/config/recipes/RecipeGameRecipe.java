package crazypants.enderio.base.config.recipes;

public interface RecipeGameRecipe extends RecipeConfigElement {

  boolean isActive();

  void register();

}