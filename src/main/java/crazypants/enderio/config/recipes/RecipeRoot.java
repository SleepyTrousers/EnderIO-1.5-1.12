package crazypants.enderio.config.recipes;

public interface RecipeRoot extends RecipeGameRecipe {

  void addRecipes(RecipeRoot other);

  <T extends RecipeRoot> T copy(T in);
}
