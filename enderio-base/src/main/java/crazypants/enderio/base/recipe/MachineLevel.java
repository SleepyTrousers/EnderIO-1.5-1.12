package crazypants.enderio.base.recipe;

import javax.annotation.Nonnull;

/**
 * Just a couple of constants to make the code a bit more readable. Machines (and recipes) can now use "MachineLevel.XXX" to make clear some constant refers to
 * the machine and not the recipe.
 *
 */
public final class MachineLevel {

  public static final @Nonnull RecipeLevel SIMPLE = RecipeLevel.SIMPLE;
  public static final @Nonnull RecipeLevel NORMAL = RecipeLevel.NORMAL;
  public static final @Nonnull RecipeLevel ADVANCED = RecipeLevel.ADVANCED;
  public static final @Nonnull RecipeLevel IGNORE = RecipeLevel.IGNORE;

}