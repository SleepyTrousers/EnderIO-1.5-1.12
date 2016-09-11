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
      if (Config.crateSyntheticRecipes //
          && rec.getInputs().length == 1 && !rec.getInputs()[0].isFluid()
          && rec.getInputs()[0].getInput().stackSize <= 21
          && rec.getOutputs().length == 1 && !rec.getOutputs()[0].isFluid() //
          && rec.getOutputs()[0].getOutput().stackSize <= 21) {

        IRecipe dupe = getRecipeForInputs(rec.getInputStacks());
        if (dupe != null) {
          // do it here because we will add "dupes" and the check need to be
          // done on the supplied recipe---which is added last
          Log.warn("The supplied recipe " + rec + " for " + managerName + " may be a duplicate to: " + dupe);
        }

        int er = rec.getEnergyRequired();
        RecipeBonusType bns = rec.getBonusType();
        RecipeOutput out = rec.getOutputs()[0];
        RecipeInput in = rec.getInputs()[0];

        RecipeInput in2 = in.copy();
        in2.getInput().stackSize *= 2;
        RecipeOutput out2 = new RecipeOutput(out.getOutput(), out.getChance(), out.getExperiance());
        out2.getOutput().stackSize *= 2;

        RecipeInput in3 = in.copy();
        in3.getInput().stackSize *= 3;
        RecipeOutput out3 = new RecipeOutput(out.getOutput(), out.getChance(), out.getExperiance());
        out3.getOutput().stackSize *= 3;

        recipes.add(new BasicManyToOneRecipe(new Recipe(out3, er * 3, bns, new RecipeInput[] { in.copy(), in.copy(), in.copy() })));
        recipes.add(new BasicManyToOneRecipe(new Recipe(out3, er * 3, bns, new RecipeInput[] { in.copy(), in2.copy() })));
        recipes.add(new BasicManyToOneRecipe(new Recipe(out3, er * 3, bns, new RecipeInput[] { in2.copy(), in.copy() })));
        recipes.add(new BasicManyToOneRecipe(new Recipe(out2, er * 2, bns, new RecipeInput[] { in.copy(), in.copy() })));
        recipes.add(new BasicManyToOneRecipe(new Recipe(out3, er * 3, bns, new RecipeInput[] { in3.copy() })));
        recipes.add(new BasicManyToOneRecipe(new Recipe(out2, er * 2, bns, new RecipeInput[] { in2.copy() })));
        recipes.add(new BasicManyToOneRecipe(rec));
        Log.info("Created 6 synthetic recipes for " + in.getInput() + " => " + out.getOutput());
      } else {
        addRecipe(new BasicManyToOneRecipe(rec));
      }
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
      Log.warn("The supplied recipe " + recipe + " for " + managerName + " may be a duplicate to: " + rec);
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
