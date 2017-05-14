package crazypants.enderio.integration.jei;

import javax.annotation.Nonnull;

public class RecipeHandler<T extends RecipeWrapper> extends BaseRecipeHandler<T> {

  public RecipeHandler(@Nonnull Class<T> clazz, @Nonnull String uid) {
    super(clazz, uid);
  }

  @Override
  public boolean isRecipeValid(@Nonnull T recipe) {
    return recipe.isValid();
  }

}
