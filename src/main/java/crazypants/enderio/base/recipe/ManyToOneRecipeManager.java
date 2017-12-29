package crazypants.enderio.base.recipe;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.Util;

import crazypants.enderio.base.Log;
import crazypants.enderio.base.config.Config;
import crazypants.enderio.base.integration.tic.TicProxy;
import crazypants.enderio.util.Prep;
import net.minecraft.item.ItemStack;

public class ManyToOneRecipeManager {
  
  private final @Nonnull NNList<IManyToOneRecipe> recipes = new NNList<IManyToOneRecipe>();

  private final @Nonnull String coreFileName;
  private final @Nonnull String customFileName;
  private final @Nonnull String managerName;

  public ManyToOneRecipeManager(@Nonnull String coreFileName, @Nonnull String custonFileName, @Nonnull String managerName) {
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
      throw new RuntimeException("Could not load recipes for " + managerName + ". See logfile for more information.");
    }
  }

  protected CustomTagHandler createCustomTagHandler() {
    return null;
  }

  public void addCustomRecipes(@Nonnull String xmlDef) {
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

  public @Nonnull NNList<IManyToOneRecipe> getRecipes() {
    return recipes;
  }

  private void processConfig(@Nonnull RecipeConfig config) {
    if(config.isDumpItemRegistery()) {
      Util.dumpModObjects(new File(Config.configDirectory, "modObjectsRegistery.txt"));
    }
    if(config.isDumpOreDictionary()) {
      Util.dumpOreNames(new File(Config.configDirectory, "oreDictionaryRegistery.txt"));
    }
    List<Recipe> newRecipes = config.getRecipes(false);
    Log.info("Found " + newRecipes.size() + " valid " + managerName + " recipes in config.");
    for (Recipe rec : newRecipes) {
      addRecipe(rec);
    }
    Log.info("Finished processing " + managerName + " recipes. " + recipes.size() + " recipes avaliable.");
  }

  public void addRecipe(Recipe rec) {
    if (rec == null) {
      Log.warn("Invalid null recipe found for " + managerName);
    } else if (Config.createSyntheticRecipes //
        && rec.getInputs().length == 1 && !rec.getInputs()[0].isFluid()
        && rec.getInputs()[0].getInput().getCount() <= (rec.getInputs()[0].getInput().getMaxStackSize() / 3)
        && rec.getOutputs().length == 1 && !rec.getOutputs()[0].isFluid() //
        && rec.getOutputs()[0].getOutput().getCount() <= (rec.getOutputs()[0].getOutput().getMaxStackSize() / 3)) {

      IRecipe dupe = getRecipeForInputs(rec.getInputStacks());
      if (dupe != null) {
        // do it here because we will add "dupes" and the check need to be
        // done on the supplied recipe---which is added last
        Log.warn("The supplied recipe " + rec + " for " + managerName + " may be a duplicate to: " + dupe);
      }

      int er = rec.getEnergyRequired();
      RecipeBonusType bns = rec.getBonusType();
      RecipeOutput out = rec.getOutputs()[0];
      IRecipeInput in = rec.getInputs()[0];

      IRecipeInput in2 = in.copy();
      in2.getInput().grow(in2.getInput().getCount());
      RecipeOutput out2 = new RecipeOutput(out.getOutput(), out.getChance(), out.getExperiance());
      out2.getOutput().grow(out2.getOutput().getCount());

      IRecipeInput in3 = in.copy();
      in3.getInput().grow(in3.getInput().getCount());
      in3.getInput().grow(in3.getInput().getCount());
      RecipeOutput out3 = new RecipeOutput(out.getOutput(), out.getChance(), out.getExperiance());
      out3.getOutput().grow(out3.getOutput().getCount());
      out3.getOutput().grow(out3.getOutput().getCount());

      recipes.add(new BasicManyToOneRecipe(new Recipe(out3, er * 3, bns, new IRecipeInput[] { in.copy(), in.copy(), in.copy() })).setSynthetic());
      recipes.add(new BasicManyToOneRecipe(new Recipe(out3, er * 3, bns, new IRecipeInput[] { in.copy(), in2.copy() })).setSynthetic());
      recipes.add(new BasicManyToOneRecipe(new Recipe(out3, er * 3, bns, new IRecipeInput[] { in2.copy(), in.copy() })).setSynthetic());
      recipes.add(new BasicManyToOneRecipe(new Recipe(out2, er * 2, bns, new IRecipeInput[] { in.copy(), in.copy() })).setSynthetic());
      recipes.add(new BasicManyToOneRecipe(new Recipe(out3, er * 3, bns, new IRecipeInput[] { in3.copy() })).setSynthetic());
      recipes.add(new BasicManyToOneRecipe(new Recipe(out2, er * 2, bns, new IRecipeInput[] { in2.copy() })).setSynthetic());
      recipes.add(new BasicManyToOneRecipe(rec));
      Log.info("Created 6 synthetic recipes for " + in.getInput() + " => " + out.getOutput());
    } else {
      addRecipe(new BasicManyToOneRecipe(rec));
      if (managerName.equals("Alloy Smelter") && rec.getInputs().length >= 2) {
        ItemStack[] ins = new ItemStack[rec.getInputs().length];
        for (int i = 0; i < rec.getInputs().length; i++) {
          ins[i] = rec.getInputs()[i].getInput();
        }
        TicProxy.registerAlloyRecipe(rec.getOutputs()[0].getOutput(), ins);
      }
    }
  }

  public void addRecipe(@Nonnull IManyToOneRecipe recipe) {
    IRecipe rec = getRecipeForInputs(recipe.getInputStacks());
    if(rec != null) {
      Log.warn("The supplied recipe " + recipe + " for " + managerName + " may be a duplicate to: " + rec);
    }
    recipes.add(recipe);
  }

  private IRecipe getRecipeForInputs(@Nonnull NNList<ItemStack> inputs) {
    MachineRecipeInput[] ins = new MachineRecipeInput[inputs.size()];

    for (int i = 0; i < inputs.size(); i++) {
      ins[i] = new MachineRecipeInput(-1, inputs.get(i));
    }
    return getRecipeForInputs(ins);
  }

  public IRecipe getRecipeForInputs(@Nonnull MachineRecipeInput[] inputs) {

    for (IManyToOneRecipe rec : recipes) {
      if(rec.isInputForRecipe(inputs)) {
        return rec;
      }
    }
    return null;
  }

  public boolean isValidInput(@Nonnull MachineRecipeInput input) {
    if (Prep.isInvalid(input.item)) {
      return false;
    }
    for (IManyToOneRecipe recipe : recipes) {
      for (IRecipeInput ri : recipe.getInputs()) {
        if(ri.isInput(input.item) && (ri.getSlotNumber() == -1 || input.slotNumber == ri.getSlotNumber())) {
          return true;
        }
      }
    }
    return false;
  }

  public boolean isValidRecipeComponents(@Nonnull ItemStack[] inputs) {
    if (inputs.length == 0) {
      return false;
    }
    for (IManyToOneRecipe recipe : recipes) {
      if(recipe.isValidRecipeComponents(inputs)) {
        return true;
      }
    }
    return false;
  }

  @Nonnull
  public List<IManyToOneRecipe> getRecipesThatHaveTheseAsValidRecipeComponents(@Nonnull ItemStack[] inputs) {
    List<IManyToOneRecipe> result = new ArrayList<IManyToOneRecipe>();
    if (inputs.length > 0) {
      for (IManyToOneRecipe recipe : recipes) {
        if (recipe.isValidRecipeComponents(inputs)) {
          result.add(recipe);
        }
      }
    }
    return result;
  }

  public float getExperianceForOutput(@Nonnull ItemStack output) {
    for (IManyToOneRecipe recipe : recipes) {
      if(recipe.getOutput().getItem() == output.getItem() && recipe.getOutput().getItemDamage() == output.getItemDamage()) {
        return recipe.getOutputs()[0].getExperiance();
      }
    }
    return 0;
  }
  
}
