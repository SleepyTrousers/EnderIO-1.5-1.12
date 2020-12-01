package crazypants.enderio.base.recipe.lookup;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NNList.NNIterator;

import crazypants.enderio.base.recipe.MachineRecipeInput;
import crazypants.enderio.util.FuncUtil;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class TriItemLookup<REC> implements Iterable<REC> {

  private final @Nonnull ItemRecipeNode<REC, ItemRecipeNode<REC, ItemRecipeLeafNode<REC>>> root;

  public TriItemLookup() {
    root = makeNode1();
  }

  private @Nonnull ItemRecipeNode<REC, ItemRecipeNode<REC, ItemRecipeLeafNode<REC>>> makeNode1() {
    return new ItemRecipeNode<>();
  }

  private @Nonnull ItemRecipeNode<REC, ItemRecipeLeafNode<REC>> makeNode2() {
    return new ItemRecipeNode<>();
  }

  private @Nonnull ItemRecipeLeafNode<REC> makeNode3() {
    return new ItemRecipeLeafNode<>();
  }

  public void addRecipe(@Nonnull REC recipe, @Nonnull Item key1, @Nonnull Item key2, @Nonnull Item key3) {
    if (key1 == Items.AIR) {
      addRecipe(recipe, key2, key3);
    } else if (key2 == Items.AIR) {
      addRecipe(recipe, key1, key3);
    } else if (key3 == Items.AIR) {
      addRecipe(recipe, key1, key2);
    } else {
      root.makeNext(recipe, key1, this::makeNode2).makeNext(recipe, key2, this::makeNode3).addRecipe(recipe, key3);
    }
  }

  public void addRecipe(@Nonnull REC recipe, @Nonnull Item key1, @Nonnull Item key2) {
    if (key1 == Items.AIR) {
      addRecipe(recipe, key2);
    } else if (key2 == Items.AIR) {
      addRecipe(recipe, key1);
    } else {
      root.makeNext(recipe, key1, this::makeNode2).makeNext(recipe, key2, this::makeNode3);
    }
  }

  public void addRecipe(@Nonnull REC recipe, @Nonnull Item key1) {
    if (key1 == Items.AIR) {
      throw new RuntimeException("no items to store recipe for");
    } else {
      root.makeNext(recipe, key1, this::makeNode2);
    }
  }

  public @Nonnull NNList<REC> getRecipesL(@Nonnull NNList<ItemStack> list) {
    Item key1 = list.size() >= 1 ? list.get(0).getItem() : Items.AIR;
    Item key2 = list.size() >= 2 ? list.get(1).getItem() : Items.AIR;
    Item key3 = list.size() >= 3 ? list.get(2).getItem() : Items.AIR;
    return getRecipes3(key1, key2, key3);
  }

  public @Nonnull NNList<REC> getRecipesLMRI(@Nonnull NNList<MachineRecipeInput> list) {
    Item key1 = list.size() >= 1 ? list.get(0).item.getItem() : Items.AIR;
    Item key2 = list.size() >= 2 ? list.get(1).item.getItem() : Items.AIR;
    Item key3 = list.size() >= 3 ? list.get(2).item.getItem() : Items.AIR;
    return getRecipes3(key1, key2, key3);
  }

  public @Nonnull NNList<REC> getRecipes3(@Nonnull Item key1, @Nonnull Item key2, @Nonnull Item key3) {
    if (key1 == Items.AIR) { // ----------- 0xx
      if (key2 == Items.AIR) { // --------- 00x
        if (key3 == Items.AIR) { // ------- 000
          return NNList.emptyList();
        } else { // ----------------------- 001
          return getRecipes(key3);
        }
      } else if (key3 == Items.AIR) { // -- 010
        return getRecipes(key2);
      } else { // ------------------------- 011
        return getRecipes(key2, key3);
      }
    } else if (key2 == Items.AIR) { // ---- 10x
      if (key3 == Items.AIR) { // --------- 100
        return getRecipes(key1);
      } else { // ------------------------- 101
        return getRecipes(key1, key3);
      }
    } else if (key3 == Items.AIR) { // ---- 110
      return getRecipes(key1, key2);
    } else { // --------------------------- 111
      return getRecipes(key1, key2, key3);
    }
  }

  public @Nonnull NNList<REC> getRecipes(@Nonnull Item key1, @Nonnull Item key2, @Nonnull Item key3) {
    return FuncUtil.runIfNN(root.getNext(key1), r2 -> FuncUtil.runIf(r2.getNext(key2), r3 -> r3.getRecipes(key3)), () -> NNList.emptyList());
  }

  public @Nonnull NNList<REC> getRecipes(@Nonnull Item key1, @Nonnull Item key2) {
    return FuncUtil.runIfNN(root.getNext(key1), r2 -> r2.getRecipes(key2), () -> NNList.emptyList());
  }

  public @Nonnull NNList<REC> getRecipes(@Nonnull Item key1) {
    return root.getRecipes(key1);
  }

  @Override
  public NNIterator<REC> iterator() {
    // root has all recipes
    return root.iterator();
  }

}
