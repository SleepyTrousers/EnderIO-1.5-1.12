package crazypants.enderio.base.recipe.lookup;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NullHelper;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.item.Item;

public class ItemRecipeLeafNode<REC> implements IRecipeNode<REC, Item, Integer> {

  private final @Nonnull Int2ObjectOpenHashMap<NNList<REC>> map = new Int2ObjectOpenHashMap<>();

  public ItemRecipeLeafNode() {
  }

  @Override
  public @Nonnull NNList<REC> getRecipes(@Nonnull Item key) {
    return NullHelper.first(map.get(Item.getIdFromItem(key)), NNList::emptyList);
  }

  public void addRecipe(@Nonnull REC recipe, @Nonnull Item key) {
    NNList<REC> recipes = map.computeIfAbsent(Item.getIdFromItem(key), unused -> new NNList<>());
    if (!recipes.contains(recipe)) {
      recipes.add(recipe);
    }
  }

}
