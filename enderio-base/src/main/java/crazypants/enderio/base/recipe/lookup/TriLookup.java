package crazypants.enderio.base.recipe.lookup;

import java.util.function.Function;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;

import crazypants.enderio.util.FuncUtil;

public class TriLookup<REC, LOB1, LID1, LOB2, LID2, LOB3, LID3> {

  private final @Nonnull Function<LOB1, LID1> toId1;
  private final @Nonnull Function<LOB2, LID2> toId2;
  private final @Nonnull Function<LOB3, LID3> toId3;

  private final @Nonnull RecipeNode<REC, LOB1, LID1, RecipeNode<REC, LOB2, LID2, RecipeLeafNode<REC, LOB3, LID3>>> root;

  public TriLookup(@Nonnull Function<LOB1, LID1> toId1, @Nonnull Function<LOB2, LID2> toId2, @Nonnull Function<LOB3, LID3> toId3) {
    this.toId1 = toId1;
    this.toId2 = toId2;
    this.toId3 = toId3;
    root = makeNode1();
  }

  private @Nonnull RecipeNode<REC, LOB1, LID1, RecipeNode<REC, LOB2, LID2, RecipeLeafNode<REC, LOB3, LID3>>> makeNode1() {
    return new RecipeNode<>(toId1);
  }

  private @Nonnull RecipeNode<REC, LOB2, LID2, RecipeLeafNode<REC, LOB3, LID3>> makeNode2() {
    return new RecipeNode<>(toId2);
  }

  private @Nonnull RecipeLeafNode<REC, LOB3, LID3> makeNode3() {
    return new RecipeLeafNode<>(toId3);
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
    return FuncUtil.runIfNN(root.getNext(key1), r2 -> FuncUtil.runIf(r2.getNext(key2), r3 -> r3.getRecipes(key3)), () -> NNList.emptyList());
  }

  public @Nonnull NNList<REC> getRecipes(@Nonnull LOB1 key1, @Nonnull LOB2 key2) {
    return FuncUtil.runIfNN(root.getNext(key1), r2 -> r2.getRecipes(key2), () -> NNList.emptyList());
  }

  public @Nonnull NNList<REC> getRecipes(@Nonnull LOB1 key1) {
    return root.getRecipes(key1);
  }

}
