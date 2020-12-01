package crazypants.enderio.base.recipe.lookup;

import java.util.function.Function;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NullHelper;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

public class IntRecipeLeafNode<REC, LOB> implements IRecipeNode<REC, LOB, Integer> {

  private final @Nonnull Function<LOB, Integer> toId;
  private final @Nonnull Int2ObjectOpenHashMap<NNList<REC>> map = new Int2ObjectOpenHashMap<>();

  public IntRecipeLeafNode(@Nonnull Function<LOB, Integer> toId) {
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
