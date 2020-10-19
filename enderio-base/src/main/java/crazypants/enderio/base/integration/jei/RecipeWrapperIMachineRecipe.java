package crazypants.enderio.base.integration.jei;

import crazypants.enderio.base.recipe.IMachineRecipe;
import crazypants.enderio.base.recipe.RecipeLevel;

public abstract class RecipeWrapperIMachineRecipe<E extends IMachineRecipe> extends RecipeWrapperBase {

  protected final E recipe;

  public RecipeWrapperIMachineRecipe(E recipe) {
    this.recipe = recipe;
  }

  @Override
  protected RecipeLevel getRecipeLevel() {
    return recipe.getRecipeLevel();
  }

}