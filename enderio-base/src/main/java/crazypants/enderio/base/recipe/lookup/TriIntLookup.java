package crazypants.enderio.base.recipe.lookup;

import java.util.function.Function;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;

import crazypants.enderio.util.FuncUtil;

public class TriIntLookup<REC, LOB1, LOB2, LOB3> {

  private final @Nonnull Function<LOB1, Integer> toId1;
  private final @Nonnull Function<LOB2, Integer> toId2;
  private final @Nonnull Function<LOB3, Integer> toId3;

  private final @Nonnull IntRecipeNode<REC, LOB1, IntRecipeNode<REC, LOB2, IntRecipeLeafNode<REC, LOB3>>> root;

  public TriIntLookup(@Nonnull Function<LOB1, Integer> toId1, @Nonnull Function<LOB2, Integer> toId2, @Nonnull Function<LOB3, Integer> toId3) {
    this.toId1 = toId1;
    this.toId2 = toId2;
    this.toId3 = toId3;
    root = makeNode1();
  }

  private @Nonnull IntRecipeNode<REC, LOB1, IntRecipeNode<REC, LOB2, IntRecipeLeafNode<REC, LOB3>>> makeNode1() {
    return new IntRecipeNode<>(toId1);
  }

  private @Nonnull IntRecipeNode<REC, LOB2, IntRecipeLeafNode<REC, LOB3>> makeNode2() {
    return new IntRecipeNode<>(toId2);
  }

  private @Nonnull IntRecipeLeafNode<REC, LOB3> makeNode3() {
    return new IntRecipeLeafNode<>(toId3);
  }

  public void addRecipe(@Nonnull REC recipe, @Nonnull LOB1 key1, @Nonnull LOB2 key2, @Nonnull LOB3 key3) {
    root.makeNext(recipe, key1, this::makeNode2).makeNext(recipe, key2, this::makeNode3).addRecipe(recipe, key3);
  }

  public void addRecipe(@Nonnull REC recipe, @Nonnull LOB1 key1, @Nonnull LOB2 key2) {
    root.makeNext(recipe, key1, this::makeNode2).makeNext(recipe, key2, this::makeNode3);
  }

  public void addRecipe(@Nonnull REC recipe, @Nonnull LOB1 key1) {
    root.makeNext(recipe, key1, this::makeNode2);
  }

  public @Nonnull NNList<REC> getRecipes(@Nonnull LOB1 key1, @Nonnull LOB2 key2, @Nonnull LOB3 key3) {
    return FuncUtil.runIfOrSupNN(root.getNext(key1), r2 -> FuncUtil.runIf(r2.getNext(key2), r3 -> r3.getRecipes(key3)), () -> NNList.emptyList());
  }

  public @Nonnull NNList<REC> getRecipes(@Nonnull LOB1 key1, @Nonnull LOB2 key2) {
    return FuncUtil.runIfOrSupNN(root.getNext(key1), r2 -> r2.getRecipes(key2), () -> NNList.emptyList());
  }

  public @Nonnull NNList<REC> getRecipes(@Nonnull LOB1 key1) {
    return root.getRecipes(key1);
  }

}
