package crazypants.enderio.base.recipe;

import javax.annotation.Nonnull;

public enum RecipeLevel {
  SIMPLE,
  NORMAL,
  ADVANCED,
  IGNORE {
    @Override
    public boolean canBeMadeIn(@Nonnull RecipeLevel machine) {
      return true;
    }

    @Override
    public boolean canMake(@Nonnull RecipeLevel recipe) {
      return true;
    }
  };

  public boolean canMake(@Nonnull RecipeLevel recipe) {
    return recipe == IGNORE || recipe.ordinal() <= ordinal();
  }

  public boolean canBeMadeIn(@Nonnull RecipeLevel machine) {
    return machine.canMake(this);
  }

}