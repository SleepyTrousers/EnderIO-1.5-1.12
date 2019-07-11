package crazypants.enderio.base.config.recipes;

import java.util.List;

import javax.annotation.Nonnull;

import crazypants.enderio.base.config.recipes.xml.AbstractConditional;

public interface IRecipeRoot extends IRecipeGameRecipe {

  public enum Overrides {
    ALLOW,
    DENY,
    WARN;
  }

  @Nonnull
  <T extends IRecipeRoot> T addRecipes(@Nonnull IRecipeRoot other, @Nonnull Overrides overrides) throws InvalidRecipeConfigException;

  @Nonnull
  List<AbstractConditional> getRecipes();

}
