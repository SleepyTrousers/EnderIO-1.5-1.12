package crazypants.enderio.machine.recipe;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;

import com.enderio.core.common.util.Util;

import crazypants.enderio.Log;
import crazypants.enderio.config.Config;
import crazypants.enderio.machine.MachineRecipeInput;

public class ManyToOneRecipeManager {
  
  private final List<IManyToOneRecipe> recipes = new ArrayList<IManyToOneRecipe>();

  private final String coreFileName;
  private final String customFileName;
  private final String managerName;

  public ManyToOneRecipeManager(String coreFileName, String custonFileName, String managerName) {  
    this.coreFileName = coreFileName;
    this.customFileName = custonFileName;
    this.managerName = managerName;
  }

  public void loadRecipesFromConfig() {
    CustomTagHandler tagHandler = createCustomTagHandler();
    RecipeConfig config = RecipeConfig.loadRecipeConfig(coreFileName, customFileName, tagHandler);
    if(config != null) {
      processConfig(config);
      if(tagHandler != null) {
        tagHandler.configProcessed();
      }
    } else {
      Log.error("Could not load recipes for " + managerName + ".");
    }
  }

  protected CustomTagHandler createCustomTagHandler() {
    return null;
  }

  public void addCustomRecipes(String xmlDef) {
    RecipeConfig config;
    CustomTagHandler tagHandler = createCustomTagHandler();
    try {
      config = RecipeConfigParser.parse(xmlDef, tagHandler);
    } catch (Exception e) {
      Log.error("Error parsing custom xml for " + managerName );
      return;
    }

    if(config == null) {
      Log.error("Could not process custom XML " + managerName);
      return;
    }
    processConfig(config);
    if(tagHandler != null) {
      tagHandler.configProcessed();
    }
  }

  public List<IManyToOneRecipe> getRecipes() {
    return recipes;
  }

  private void processConfig(RecipeConfig config) {
    if(config.isDumpItemRegistery()) {
      Util.dumpModObjects(new File(Config.configDirectory, "modObjectsRegistery.txt"));
    }
    if(config.isDumpOreDictionary()) {
      Util.dumpOreNames(new File(Config.configDirectory, "oreDictionaryRegistery.txt"));
    }
    List<Recipe> newRecipes = config.getRecipes(false);
    Log.info("Found " + newRecipes.size() + " valid " + managerName + " recipes in config.");
    for (Recipe rec : newRecipes) {
      addRecipe(new BasicManyToOneRecipe(rec));
    }    
    Log.info("Finished processing " + managerName + " recipes. " + recipes.size() + " recipes avaliable.");
  }

  public void addRecipe(IManyToOneRecipe recipe) {
    if(recipe == null) {
      Log.debug("Could not add invalid recipe: " + recipe + " for " + managerName );
      return;
    }
    IRecipe rec = getRecipeForInputs(recipe.getInputStacks());
    if(rec != null) {
      Log.warn("Not adding supplied recipe to " + managerName + " as a recipe already exists for the inputs: " + recipe);
      return;
    }
    recipes.add(recipe);
  }

  private IRecipe getRecipeForInputs(List<ItemStack> inputs) {
    MachineRecipeInput[] ins = new MachineRecipeInput[inputs.size()];

    for (int i = 0; i < inputs.size(); i++) {
      ins[i] = new MachineRecipeInput(-1, inputs.get(i));
    }
    return getRecipeForInputs(ins);
  }

  public IRecipe getRecipeForInputs(MachineRecipeInput[] inputs) {

    for (IManyToOneRecipe rec : recipes) {
      if(rec.isInputForRecipe(inputs)) {
        return rec;
      }
    }
    return null;
  }

  public boolean isValidInput(MachineRecipeInput input) {
    if(input == null || input.item == null) {
      return false;
    }
    for (IManyToOneRecipe recipe : recipes) {
      for (RecipeInput ri : recipe.getInputs()) {
        if(ri.isInput(input.item) && (ri.getSlotNumber() == -1 || input.slotNumber == ri.getSlotNumber())) {
          return true;
        }
      }
    }
    return false;
  }

  public boolean isValidRecipeComponents(ItemStack[] inputs) {
    if(inputs == null || inputs.length == 0) {
      return false;
    }
    for (IManyToOneRecipe recipe : recipes) {
      if(recipe.isValidRecipeComponents(inputs)) {
        return true;
      }
    }
    return false;
  }

  public float getExperianceForOutput(ItemStack output) {
    for (IManyToOneRecipe recipe : recipes) {
      if(recipe.getOutput().getItem() == output.getItem() && recipe.getOutput().getItemDamage() == output.getItemDamage()) {
        return recipe.getOutputs()[0].getExperiance();
      }
    }
    return 0;
  }
  
}
