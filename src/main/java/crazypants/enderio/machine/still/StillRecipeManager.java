package crazypants.enderio.machine.still;

import java.util.ArrayList;
import java.util.List;

import crazypants.enderio.Log;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.MachineRecipeInput;
import crazypants.enderio.machine.MachineRecipeRegistry;
import crazypants.enderio.machine.recipe.IRecipe;
import crazypants.enderio.machine.recipe.Recipe;
import crazypants.enderio.machine.recipe.RecipeConfig;
import crazypants.enderio.machine.recipe.RecipeConfigParser;

public class StillRecipeManager {

  private static final String CORE_FILE_NAME = "StillRecipes_Core.xml";
  private static final String CUSTOM_FILE_NAME = "StillRecipes_User.xml";

  static final StillRecipeManager instance = new StillRecipeManager();

  public static StillRecipeManager getInstance() {
    return instance;
  }

  private final List<IRecipe> recipes = new ArrayList<IRecipe>();

  public StillRecipeManager() {
  }

  public void loadRecipesFromConfig() {
    RecipeConfig config = RecipeConfig.loadRecipeConfig(CORE_FILE_NAME, CUSTOM_FILE_NAME, null);
    if(config != null) {
      processConfig(config);
    } else {
      Log.error("Could not load recipes for SAG Mill.");
    }

    MachineRecipeRegistry.instance.registerRecipe(ModObject.blockStill.unlocalisedName, new StillMachineRecipe());

  }

  public void addCustumRecipes(String xmlDef) {
    RecipeConfig config;
    try {
      config = RecipeConfigParser.parse(xmlDef, null);
    } catch (Exception e) {
      Log.error("Error parsing custom xml");
      return;
    }

    if(config == null) {
      Log.error("Could process custom XML");
      return;
    }
    processConfig(config);
  }

  public IRecipe getRecipeForInput(MachineRecipeInput[] inputs) {
    if(inputs == null || inputs.length == 0) {
      return null;
    }
    for (IRecipe recipe : recipes) {
      if(recipe.isInputForRecipe(inputs)) {
        return recipe;
      }
    }
    return null;
  }

  private void processConfig(RecipeConfig config) {

    List<Recipe> newRecipes = config.getRecipes(false);
    Log.info("Found " + newRecipes.size() + " valid Still recipes in config.");
    for (Recipe rec : newRecipes) {
      addRecipe(rec);
    }
    Log.info("Finished processing Still recipes. " + recipes.size() + " recipes avaliable.");
  }

  public void addRecipe(IRecipe recipe) {
    if(recipe == null || !recipe.isValid()) {
      Log.debug("Could not add invalid recipe: " + recipe);
      return;
    }
    recipes.add(new StillRecipe(recipe));
  }

  public List<IRecipe> getRecipes() {
    return recipes;
  }

  public boolean isValidInput(MachineRecipeInput input) {
    for (IRecipe recipe : recipes) {
      if(input.item != null && recipe.isValidInput(input.slotNumber, input.item)) {
        return true;
      } else if(input.fluid != null && recipe.isValidInput(input.fluid)) {
        return true;
      }
    }
    return false;
  }

}
