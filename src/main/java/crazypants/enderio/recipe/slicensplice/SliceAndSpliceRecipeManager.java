package crazypants.enderio.recipe.slicensplice;

import crazypants.enderio.recipe.MachineRecipeRegistry;
import crazypants.enderio.recipe.ManyToOneMachineRecipe;
import crazypants.enderio.recipe.ManyToOneRecipeManager;

public class SliceAndSpliceRecipeManager extends ManyToOneRecipeManager {

  static final SliceAndSpliceRecipeManager instance = new SliceAndSpliceRecipeManager();

  public static SliceAndSpliceRecipeManager getInstance() {
    return instance;
  }

  public SliceAndSpliceRecipeManager() {
    super("slice_and_splice_recipes_core.xml", "slice_and_splice_recipes_user.xml", "Slice'N'Splice");
  }

  @Override
  public void loadRecipesFromConfig() {
    super.loadRecipesFromConfig();
    MachineRecipeRegistry.instance.registerRecipe(MachineRecipeRegistry.SLICENSPLICE,
        new ManyToOneMachineRecipe("SpliceAndSpliceRecipe", MachineRecipeRegistry.SLICENSPLICE, this));
  }

}
