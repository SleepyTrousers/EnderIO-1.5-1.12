package crazypants.enderio.base.config.recipes;

public interface RecipeRoot extends RecipeGameRecipe {

  public enum Overrides {
    ALLOW,
    DENY,
    WARN;
  }

  <T extends RecipeRoot> T addRecipes(RecipeRoot other, Overrides overrides) throws InvalidRecipeConfigException;

}
