package crazypants.enderio.base.recipe.lookup;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Supplier;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NNList.NNIterator;
import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.util.FuncUtil;
import crazypants.enderio.util.NNPair;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.item.Item;

public class ItemRecipeNode<REC, CHL extends IRecipeNode<?, ?, ?>> implements IRecipeNode<REC, Item, Integer>, Iterable<REC> {

  private final @Nonnull Int2ObjectOpenHashMap<NNPair<NNList<REC>, CHL>> map = new Int2ObjectOpenHashMap<>();

  public ItemRecipeNode() {
  }

  @Override
  public @Nonnull NNList<REC> getRecipes(@Nonnull Item key) {
    return FuncUtil.runIfOrNN(map.get(Item.getIdFromItem(key)), p -> p.getLeft(), NNList.emptyList());
  }

  public CHL getNext(@Nonnull Item key) {
    return FuncUtil.runIf(map.get(Item.getIdFromItem(key)), p -> p.getRight());
  }

  public @Nonnull CHL makeNext(@Nonnull REC recipe, @Nonnull Item key, Supplier<CHL> maker) {
    NNPair<NNList<REC>, CHL> next = map.computeIfAbsent(Item.getIdFromItem(key),
        unused -> new NNPair<>(new NNList<>(), NullHelper.notnull(maker.get(), "bad maker")));
    if (!next.getLeft().contains(recipe)) {
      next.getLeft().add(recipe);
    }
    return next.getRight();
  }

  private final NNIterator<REC> NONE = new NNIterator<REC>() {

    @Override
    public boolean hasNext() {
      return false;
    }

    @Override
    public @Nonnull REC next() {
      throw new NullPointerException();
    }
  };

  private class RecipeIterator implements NNIterator<REC> {

    private final Iterator<NNPair<NNList<REC>, CHL>> i0 = map.values().iterator();
    private NNIterator<REC> i1 = NONE;

    private Set<REC> seen = new HashSet<>();
    private REC next = null;

    private RecipeIterator() {
      findNext();
    }

    private void findNext() {
      while (true) {
        next = null;
        while (!i1.hasNext() && i0.hasNext()) {
          i1 = i0.next().getLeft().fastIterator();
        }
        if (!i1.hasNext()) {
          return;
        }
        next = i1.next();
        if (!seen.contains(next)) {
          seen.add(next);
          return;
        }
      }
    }

    @Override
    public boolean hasNext() {
      return next != null;
    }

    @Override
    public @Nonnull REC next() {
      try {
        return NullHelper.notnull(next, "internal logic error");
      } finally {
        findNext();
      }
    }

  }

  @Override
  public NNIterator<REC> iterator() {
    return new RecipeIterator();
  }

}
