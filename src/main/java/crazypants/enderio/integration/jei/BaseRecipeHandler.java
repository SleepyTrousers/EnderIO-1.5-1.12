package crazypants.enderio.integration.jei;

import javax.annotation.Nonnull;

import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;

public class BaseRecipeHandler<T extends IRecipeWrapper> implements IRecipeHandler<T> {

  private final @Nonnull Class<T> clazz;
  private final @Nonnull String uid;
  
  public BaseRecipeHandler(@Nonnull Class<T> clazz, @Nonnull String uid) {
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
    return true;
  }

  @Override
  public @Nonnull String getRecipeCategoryUid(@Nonnull T recipe) {
    return uid;
  }

}
