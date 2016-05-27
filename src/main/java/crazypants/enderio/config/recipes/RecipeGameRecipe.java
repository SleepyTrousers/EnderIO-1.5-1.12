package crazypants.enderio.config.recipes;

public interface RecipeGameRecipe extends RecipeConfigElement {

  boolean isActive();

  void register();

}