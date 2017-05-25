package crazypants.enderio.recipe.slicensplice;

import crazypants.enderio.init.ModObject;
import crazypants.enderio.recipe.MachineRecipeRegistry;
import crazypants.enderio.recipe.ManyToOneMachineRecipe;
import crazypants.enderio.recipe.ManyToOneRecipeManager;

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
