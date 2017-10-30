package crazypants.enderio.machine.slicensplice;

import crazypants.enderio.machine.MachineObject;
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
    MachineRecipeRegistry.instance.registerRecipe(MachineObject.blockSliceAndSplice.getUnlocalisedName(), new ManyToOneMachineRecipe("SpliceAndSpliceRecipe",
        MachineObject.blockAlloySmelter.getUnlocalisedName(), this));
  }

}
