package crazypants.enderio.base.recipe.lookup;

import java.util.function.Function;
import java.util.function.Supplier;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.util.FuncUtil;
import crazypants.enderio.util.NNPair;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

public class IntRecipeNode<REC, LOB, CHL extends IRecipeNode<?, ?, ?>> implements IRecipeNode<REC, LOB, Integer> {

  private final @Nonnull Function<LOB, Integer> toId;
  private final @Nonnull Int2ObjectOpenHashMap<NNPair<NNList<REC>, CHL>> map = new Int2ObjectOpenHashMap<>();
  // Int2ObjectAVLTreeMap Int2ObjectRBTreeMap

  public IntRecipeNode(@Nonnull Function<LOB, Integer> toId) {
    this.toId = toId;
  }

  @Override
  public @Nonnull NNList<REC> getRecipes(@Nonnull LOB key) {
    return FuncUtil.runIfNN(map.get(toId.apply(key)), p -> p.getLeft(), NNList.emptyList());
  }

  public CHL getNext(@Nonnull LOB key) {
    return FuncUtil.runIf(map.get(toId.apply(key)), p -> p.getRight());
  }

  public @Nonnull CHL makeNext(@Nonnull REC recipe, @Nonnull LOB key, Supplier<CHL> maker) {
    NNPair<NNList<REC>, CHL> next = map.computeIfAbsent(toId.apply(key), unused -> new NNPair<>(new NNList<>(), NullHelper.notnull(maker.get(), "bad maker")));
    if (!next.getLeft().contains(recipe)) {
      next.getLeft().add(recipe);
    }
    return next.getRight();
  }

}
