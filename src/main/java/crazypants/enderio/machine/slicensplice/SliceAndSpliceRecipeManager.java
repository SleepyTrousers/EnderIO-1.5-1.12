package crazypants.enderio.machine.slicensplice;

import crazypants.enderio.ModObject;
import crazypants.enderio.machine.recipe.ManyToOneMachineRecipe;
import crazypants.enderio.machine.recipe.ManyToOneRecipeManager;

public class SliceAndSpliceRecipeManager extends ManyToOneRecipeManager {

  static final SliceAndSpliceRecipeManager instance = new SliceAndSpliceRecipeManager();

  public static SliceAndSpliceRecipeManager getInstance() {
    return instance;
  }

  public SliceAndSpliceRecipeManager() {
    super("SliceAndSpliceRecipes_Core.xml", "SliceAndSpliceRecipes_User.xml", "Slice'N'Splice");
  }

  @Override
  public void loadRecipesFromConfig() {
    super.loadRecipesFromConfig();
    MachineRecipeRegistry.instance.registerRecipe(ModObject.blockSliceAndSplice.getUnlocalisedName(), new ManyToOneMachineRecipe("SpliceAndSpliceRecipe",
        ModObject.blockAlloySmelter.getUnlocalisedName(), this));
  }

}
