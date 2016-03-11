package crazypants.enderio.jei;

import javax.annotation.Nonnull;

import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;

public class RecipeHandler<T extends RecipeWrapper> implements IRecipeHandler<T> {

  private final @Nonnull Class<T> clazz;
  private final @Nonnull String uid;
  
  public RecipeHandler(@Nonnull Class<T> clazz, @Nonnull String uid) {
    this.clazz = clazz;
    this.uid = uid;
  }

  @Override
  public @Nonnull Class<T> getRecipeClass() {
    return clazz;
  }

  @Override
  public @Nonnull String getRecipeCategoryUid() {
    return uid;
  }

  @Override
  public @Nonnull IRecipeWrapper getRecipeWrapper(@Nonnull T recipe) {
    return recipe;
  }

  @Override
  public boolean isRecipeValid(@Nonnull T recipe) {
    return recipe.isValid();
  }

}
