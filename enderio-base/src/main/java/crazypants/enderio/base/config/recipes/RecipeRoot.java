package crazypants.enderio.base.config.recipes;

import java.util.List;

import crazypants.enderio.base.config.recipes.xml.AbstractConditional;

public interface RecipeRoot extends RecipeGameRecipe {

  public enum Overrides {
    ALLOW,
    DENY,
    WARN;
  }

  <T extends RecipeRoot> T addRecipes(RecipeRoot other, Overrides overrides) throws InvalidRecipeConfigException;

  List<AbstractConditional> getRecipes();

}
