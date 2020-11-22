package crazypants.enderio.base.config.recipes;

import javax.annotation.Nonnull;

import crazypants.enderio.base.recipe.RecipeLevel;

public interface IRecipeGameRecipe extends IRecipeConfigElement {

  /**
   * Checks the recipe's conditions to see if it is enabled.
   * 
   * @return <code>true</code> if the recipe is enabled and should be registered
   */
  boolean isActive();

  /**
   * Register the recipe or recipe element with the relevant registry.
   * <p>
   * Please note that the parameters are used to be able to define some attributes on a relatively high level of the xml. The first level that has access to the
   * data will fill it in and call the next lower level, which may pass it on to an even lower level.
   * 
   * @param recipeName
   *          The name as given (or computed) by the next level up
   * @param recipeLevel
   *          The level of the recipe as given (or computed) by the next level up
   */
  void register(@Nonnull String recipeName, @Nonnull RecipeLevel recipeLevel);

  /**
   * Remove the registration(s) done by {@link #register(String, RecipeLevel)}.
   * <p>
   * Not all recipe types need to support this on a recipe level, this can be done by wiping whole registries on a top level instead.
   * <p>
   * Note that this may be called on recipe elements that have not been registered. The element should keep a reference to its registration around and not rely
   * on other values. This information should be cleared when unregistering.
   */
  default void unregister() {
  }

  /**
   * 
   * @return the configured name if there is such a thing. Recipe elements that don't have names don't need to implement this---the elements containing them
   *         should be aware of this.
   */
  default @Nonnull String getName() {
    return "unnamed";
  }

}