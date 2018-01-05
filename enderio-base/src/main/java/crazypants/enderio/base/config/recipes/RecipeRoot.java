package crazypants.enderio.base.config.recipes;

public interface RecipeRoot extends RecipeGameRecipe {

  <T extends RecipeRoot> T addRecipes(RecipeRoot other);

  <T extends RecipeRoot> T copy(T in);
}
