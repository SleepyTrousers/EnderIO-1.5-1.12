package crazypants.enderio.base.recipe.lookup;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NullHelper;

public class RecipeLeafNode<REC, LOB, LID> implements IRecipeNode<REC, LOB, LID> {

  private final @Nonnull Function<LOB, LID> toId;
  private final @Nonnull Map<LID, NNList<REC>> map = new HashMap<>();

  public RecipeLeafNode(@Nonnull Function<LOB, LID> toId) {
    this.toId = toId;
  }

  @Override
  public @Nonnull NNList<REC> getRecipes(@Nonnull LOB key) {
    return NullHelper.first(map.get(toId.apply(key)), NNList::emptyList);
  }

  public void addRecipe(@Nonnull REC recipe, @Nonnull LOB key) {
    NNList<REC> recipes = map.computeIfAbsent(toId.apply(key), unused -> new NNList<>());
    if (!recipes.contains(recipe)) {
      recipes.add(recipe);
    }
  }

}
