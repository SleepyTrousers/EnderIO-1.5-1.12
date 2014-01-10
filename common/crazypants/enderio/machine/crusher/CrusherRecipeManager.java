package crazypants.enderio.machine.crusher;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import crazypants.enderio.Config;
import crazypants.enderio.Log;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.MachineRecipeRegistry;
import crazypants.enderio.machine.recipe.IRecipe;
import crazypants.enderio.machine.recipe.Recipe;
import crazypants.enderio.machine.recipe.RecipeConfig;
import crazypants.enderio.machine.recipe.RecipeConfigParser;
import crazypants.enderio.machine.recipe.RecipeInput;
import crazypants.enderio.machine.recipe.RecipeOutput;
import crazypants.util.Util;

public class CrusherRecipeManager {

  public static final int ORE_ENERGY_COST = 400;

  public static final int INGOT_ENERGY_COST = 240;

  private static final String CORE_FILE_NAME = "SAGMillRecipes_Core.xml";
  private static final String CUSTOM_FILE_NAME = "SAGMillRecipes_User.xml";

  static final CrusherRecipeManager instance = new CrusherRecipeManager();

  public static CrusherRecipeManager getInstance() {
    return instance;
  }

  private final List<Recipe> recipes = new ArrayList<Recipe>();

  public CrusherRecipeManager() {
  }

  public void loadRecipesFromConfig() {
    RecipeConfig config = RecipeConfig.loadRecipeConfig(CORE_FILE_NAME, CUSTOM_FILE_NAME, null);
    if(config != null) {
      processConfig(config);
    } else {
      Log.error("Could not load recipes for SAG Mill.");
    }
    MachineRecipeRegistry.instance.registerRecipe(ModObject.blockCrusher.unlocalisedName, new CrusherMachineRecipe());
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

  public IRecipe getRecipeForInput(ItemStack input) {
    if(input == null) {
      return null;
    }
    for (Recipe recipe : recipes) {
      if(recipe.isInputForRecipe(new ItemStack[] { input })) {
        return recipe;
      }
    }
    return null;
  }

  private void processConfig(RecipeConfig config) {
    if(config.isDumpItemRegistery()) {
      Util.dumpModObjects(new File(Config.configDirectory, "modObjectsRegistery.txt"));
    }
    if(config.isDumpOreDictionary()) {
      Util.dumpOreNames(new File(Config.configDirectory, "oreDictionaryRegistery.txt"));
    }

    List<Recipe> newRecipes = config.getRecipes(true);
    Log.info("Added " + newRecipes.size() + " SAG Mill recipes from config.");
    for (Recipe rec : newRecipes) {
      addRecipe(rec);
    }

  }

  public void addRecipe(ItemStack input, float energyCost, ItemStack output) {
    addRecipe(input, energyCost, new RecipeOutput(output, 1));
  }

  public void addRecipe(ItemStack input, float energyCost, RecipeOutput... output) {
    if(input == null || output == null) {
      return;
    }
    addRecipe(new Recipe(new RecipeInput(input, false), energyCost, output));
  }

  public void addRecipe(Recipe recipe) {
    if(recipe == null || !recipe.isValid()) {
      Log.debug("Could not add invalid recipe: " + recipe);
      return;
    }
    IRecipe rec = getRecipeForInput(getInput(recipe));
    if(rec != null) {
      Log.warn("Not adding supplied recipe as a recipe already exists for the input: " + getInput(recipe));
      return;
    }
    recipes.add(recipe);
  }

  public List<Recipe> getRecipes() {
    return recipes;
  }

  public static ItemStack getInput(IRecipe recipe) {
    if(recipe == null || recipe.getInputs() == null || recipe.getInputs().length == 0) {
      return null;
    }
    return recipe.getInputs()[0].getInput();
  }

}
