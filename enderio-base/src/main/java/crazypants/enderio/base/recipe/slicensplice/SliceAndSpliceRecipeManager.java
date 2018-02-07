package crazypants.enderio.base.recipe.slicensplice;

import crazypants.enderio.base.recipe.MachineRecipeRegistry;
import crazypants.enderio.base.recipe.ManyToOneMachineRecipe;
import crazypants.enderio.base.recipe.ManyToOneRecipeManager;

public class SliceAndSpliceRecipeManager extends ManyToOneRecipeManager {

  static final SliceAndSpliceRecipeManager instance = new SliceAndSpliceRecipeManager();

  public static SliceAndSpliceRecipeManager getInstance() {
    return instance;
  }

  public SliceAndSpliceRecipeManager() {
    super("Slice'N'Splice");
  }

  public void create() {
    MachineRecipeRegistry.instance.registerRecipe(MachineRecipeRegistry.SLICENSPLICE,
        new ManyToOneMachineRecipe("SpliceAndSpliceRecipe", MachineRecipeRegistry.SLICENSPLICE, this));
  }

}
