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

public class VatRecipeManager {

  private static final String CORE_FILE_NAME = "VatRecipes_Core.xml";
  private static final String CUSTOM_FILE_NAME = "VatRecipes_User.xml";

  static final VatRecipeManager instance = new VatRecipeManager();

  public static VatRecipeManager getInstance() {
    return instance;
  }

  private final List<IRecipe> recipes = new ArrayList<IRecipe>();

  public VatRecipeManager() {
  }

  public void loadRecipesFromConfig() {
    RecipeConfig config = RecipeConfig.loadRecipeConfig(CORE_FILE_NAME, CUSTOM_FILE_NAME, null);
    if(config != null) {
      processConfig(config);
    } else {
      Log.error("Could not load recipes for Vat.");
    }

    MachineRecipeRegistry.instance.registerRecipe(ModObject.blockVat.unlocalisedName, new VatMachineRecipe());

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
    Log.info("Found " + newRecipes.size() + " valid Vat recipes in config.");
    for (Recipe rec : newRecipes) {
      addRecipe(rec);
    }
    Log.info("Finished processing Vat recipes. " + recipes.size() + " recipes avaliable.");
  }

  public void addRecipe(IRecipe recipe) {
    if(recipe == null || !recipe.isValid()) {
      Log.debug("Could not add invalid Vat recipe: " + recipe);
      return;
    }
    recipes.add(new VatRecipe(recipe));
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

  public boolean isValidInput(MachineRecipeInput[] inputs) {
    for (IRecipe recipe : recipes) {
      boolean allValid = true;
      String name = recipe.getOutputs()[0].getFluidOutput().getFluid().getName();
      for(MachineRecipeInput input : inputs) {
        if(input.item != null) {
          allValid = recipe.isValidInput(input.slotNumber, input.item);
        } else if(input.fluid != null) {
          allValid = recipe.isValidInput(input.fluid);
        }
        if(!allValid) {
          break;
        }
      }
      if(allValid) {
        return true;
      }
    }
    return false;
  }

}
