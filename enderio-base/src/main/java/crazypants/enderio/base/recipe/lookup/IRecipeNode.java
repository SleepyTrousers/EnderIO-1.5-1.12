package crazypants.enderio.base.recipe.lookup;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;

public interface IRecipeNode<REC, LOB, LID> {

  @Nonnull
  NNList<REC> getRecipes(@Nonnull LOB key);

}