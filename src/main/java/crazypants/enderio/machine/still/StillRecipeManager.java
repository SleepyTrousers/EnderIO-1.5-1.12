package crazypants.enderio.machine.still;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import crazypants.enderio.Log;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.MachineRecipeInput;
import crazypants.enderio.machine.MachineRecipeRegistry;
import crazypants.enderio.machine.recipe.IRecipe;
import crazypants.enderio.machine.recipe.Recipe;
import crazypants.enderio.machine.recipe.RecipeConfig;
import crazypants.enderio.machine.recipe.RecipeInput;
import crazypants.enderio.machine.recipe.RecipeOutput;

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
    //    RecipeConfig config = RecipeConfig.loadRecipeConfig(CORE_FILE_NAME, CUSTOM_FILE_NAME, null);
    //    if(config != null) {
    //      processConfig(config);
    //    } else {
    //      Log.error("Could not load recipes for SAG Mill.");
    //    }

    MachineRecipeRegistry.instance.registerRecipe(ModObject.blockStill.unlocalisedName, new StillMachineRecipe());

    RecipeInput[] ins = new RecipeInput[] {
        new RecipeInput(new FluidStack(FluidRegistry.WATER, FluidContainerRegistry.BUCKET_VOLUME), 0.5f),
        new RecipeInput(new ItemStack(Items.rotten_flesh), true, 2, 0),
        new RecipeInput(new ItemStack(Items.beef), true, 1, 0),
        new RecipeInput(new ItemStack(Items.chicken), true, 1, 0),
        new RecipeInput(new ItemStack(Items.porkchop), true, 1, 0),
        new RecipeInput(new ItemStack(Items.skull), false, 2.5f, 0),

        new RecipeInput(new ItemStack(Items.nether_wart), true, 1.5f, 1),
        new RecipeInput(new ItemStack(Items.sugar), true, 1f, 1)
    };

    FluidStack resStack = FluidRegistry.getFluidStack("nutrientdistillation", FluidContainerRegistry.BUCKET_VOLUME);
    RecipeOutput[] outs = new RecipeOutput[] {
        new RecipeOutput(resStack)
    };

    Recipe rec = new Recipe(ins, outs, 1000);
    addRecipe(new StillRecipe(rec));
  }

  public void addCustumRecipes(String xmlDef) {
    //    RecipeConfig config;
    //    try {
    //      config = RecipeConfigParser.parse(xmlDef, null);
    //    } catch (Exception e) {
    //      Log.error("Error parsing custom xml");
    //      return;
    //    }
    //
    //    if(config == null) {
    //      Log.error("Could process custom XML");
    //      return;
    //    }
    //    processConfig(config);
  }

  //  public IRecipe getRecipeForInput(List<ItemStack> input, List<FluidStack> inputFluids) {
  //    if(input == null || inputFluids == null) {
  //      return null;
  //    }
  //    for (IRecipe recipe : recipes) {
  //      if(recipe.isInputForRecipe(input, inputFluids)) {
  //        return recipe;
  //      }
  //    }
  //    return null;
  //  }

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
    //    if(config.isDumpItemRegistery()) {
    //      Util.dumpModObjects(new File(Config.configDirectory, "modObjectsRegistery.txt"));
    //    }
    //    if(config.isDumpOreDictionary()) {
    //      Util.dumpOreNames(new File(Config.configDirectory, "oreDictionaryRegistery.txt"));
    //    }
    //
    //    List<Recipe> newRecipes = config.getRecipes(true);
    //    Log.info("Found " + newRecipes.size() + " valid SAG Mill recipes in config.");
    //    for (Recipe rec : newRecipes) {
    //      addRecipe(rec);
    //    }
    //    Log.info("Finished processing Alloy Smelter recipes. " + recipes.size() + " recipes avaliable.");
  }

  public void addRecipe(IRecipe recipe) {
    if(recipe == null || !recipe.isValid()) {
      Log.debug("Could not add invalid recipe: " + recipe);
      return;
    }
    recipes.add(recipe);
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
