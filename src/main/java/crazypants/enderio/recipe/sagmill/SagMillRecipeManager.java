package crazypants.enderio.recipe.sagmill;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NNList.Callback;
import com.enderio.core.common.util.Util;

import crazypants.enderio.Log;
import crazypants.enderio.config.Config;
import crazypants.enderio.recipe.IRecipe;
import crazypants.enderio.recipe.MachineRecipeInput;
import crazypants.enderio.recipe.MachineRecipeRegistry;
import crazypants.enderio.recipe.Recipe;
import crazypants.enderio.recipe.RecipeBonusType;
import crazypants.enderio.recipe.RecipeConfig;
import crazypants.enderio.recipe.RecipeConfigParser;
import crazypants.enderio.recipe.RecipeInput;
import crazypants.enderio.recipe.RecipeOutput;
import crazypants.util.Prep;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class SagMillRecipeManager {

  public static final int ORE_ENERGY_COST = 400;

  public static final int INGOT_ENERGY_COST = 240;

  private static final @Nonnull String CORE_FILE_NAME = "SAGMillRecipes_Core.xml";
  private static final @Nonnull String CUSTOM_FILE_NAME = "SAGMillRecipes_User.xml";

  static final @Nonnull SagMillRecipeManager instance = new SagMillRecipeManager();

  public static @Nonnull SagMillRecipeManager getInstance() {
    return instance;
  }

  private final @Nonnull NNList<Recipe> recipes = new NNList<Recipe>();

  private final @Nonnull NNList<GrindingBall> balls = new NNList<GrindingBall>();

  private final @Nonnull Set<ItemStack> excludedStackCache = new HashSet<ItemStack>();

  private SagMillRecipeManager() {
  }

  public boolean isValidSagBall(@Nonnull ItemStack stack) {
    return getGrindballFromStack(stack) != null;
  }

  public boolean isExcludedFromBallBonus(@Nonnull MachineRecipeInput[] inputs) {
    if (inputs.length < 1) {
      return true;
    }
    for (MachineRecipeInput input : inputs) {
      if (Prep.isValid(input.item)) {
        if (isExcludedStack(input.item)) {
          return true;
        }
        int[] ids = OreDictionary.getOreIDs(input.item);
        if (ids != null) {
          for (int id : ids) {
            String name = OreDictionary.getOreName(id);
            if (name.startsWith("ingot") || name.startsWith("block") || name.startsWith("nugget")) {
              addExcludedStack(input.item);
              return true;
            }
          }
        }
      }
    }

    return false;
  }

  private void addExcludedStack(@Nonnull ItemStack item) {
    item = item.copy();
    item.setCount(1);
    excludedStackCache.add(item);
  }

  private boolean isExcludedStack(@Nonnull ItemStack item) {
    item = item.copy();
    item.setCount(1);
    return excludedStackCache.contains(item);
  }

  public IGrindingMultiplier getGrindballFromStack(@Nonnull ItemStack stack) {
    if (Prep.isInvalid(stack)) {
      return null;
    }
    for (GrindingBall ball : balls) {
      if (ball.isInput(stack)) {
        return ball;
      }
    }
    return null;
  }

  public boolean isValidInput(@Nonnull MachineRecipeInput input) {
    if (input.slotNumber == 1) {
      return isValidSagBall(input.item);
    }
    return getRecipeForInput(input.item) != null;
  }

  public void loadRecipesFromConfig() {
    GrindingBallTagHandler th = new GrindingBallTagHandler();
    RecipeConfig config = RecipeConfig.loadRecipeConfig(CORE_FILE_NAME, CUSTOM_FILE_NAME, th);
    balls.addAll(th.balls.values());
    Log.info("Loaded " + balls.size() + " grinding balls from SAG Mill config.");
    if (config != null) {
      processConfig(config);
    } else {
      Log.error("Could not load recipes for SAG Mill.");
    }
    MachineRecipeRegistry.instance.registerRecipe(MachineRecipeRegistry.SAGMILL, new SagMillMachineRecipe());
  }

  public void addCustomRecipes(@Nonnull String xmlDef) {
    GrindingBallTagHandler th = new GrindingBallTagHandler();
    RecipeConfig config;
    try {
      config = RecipeConfigParser.parse(xmlDef, th);
    } catch (Exception e) {
      Log.error("Error parsing custom xml");
      return;
    }

    balls.addAll(th.balls.values());
    if (config == null) {
      Log.error("Could not process custom XML");
      return;
    }
    processConfig(config);
  }

  public IRecipe getRecipeForInput(@Nonnull ItemStack input) {
    if (Prep.isInvalid(input)) {
      return null;
    }
    final MachineRecipeInput machineRecipeInput = new MachineRecipeInput(0, input);
    for (Recipe recipe : recipes) {
      if (recipe.isInputForRecipe(machineRecipeInput)) {
        return recipe;
      }
    }
    return null;
  }

  private void processConfig(@Nonnull RecipeConfig config) {
    if (config.isDumpItemRegistery()) {
      Util.dumpModObjects(new File(Config.configDirectory, "modObjectsRegistery.txt"));
    }
    if (config.isDumpOreDictionary()) {
      Util.dumpOreNames(new File(Config.configDirectory, "oreDictionaryRegistery.txt"));
    }

    NNList<Recipe> newRecipes = config.getRecipes(true);
    Log.info("Found " + newRecipes.size() + " valid SAG Mill recipes in config.");
    newRecipes.apply(new Callback<Recipe>() {
      @Override
      public void apply(@Nonnull Recipe recipe) {
        addRecipe(recipe);
      }
    });
    Log.info("Finished processing SAG Mill recipes. " + recipes.size() + " recipes avaliable.");
  }

  public void addRecipe(@Nonnull ItemStack input, int energyCost, @Nonnull ItemStack output) {
    addRecipe(input, energyCost, new RecipeOutput(output, 1));
  }

  public void addRecipe(@Nonnull ItemStack input, int energyCost, RecipeOutput... output) {
    if (Prep.isInvalid(input) || output == null) {
      return;
    }
    addRecipe(new Recipe(new RecipeInput(input, false), energyCost, RecipeBonusType.MULTIPLY_OUTPUT, output));
  }

  public void addRecipe(@Nonnull Recipe recipe) {
    if (!recipe.isValid()) {
      Log.debug("Could not add invalid recipe: " + recipe);
      return;
    }
    IRecipe rec = getRecipeForInput(getInput(recipe));
    if (rec != null) {
      Log.warn("Not adding supplied recipe as a recipe already exists for the input: " + getInput(recipe));
      return;
    }
    recipes.add(recipe);
  }

  public @Nonnull NNList<Recipe> getRecipes() {
    return recipes;
  }

  public static @Nonnull ItemStack getInput(@Nonnull IRecipe recipe) {
    if (recipe.getInputs().length == 0) {
      return Prep.getEmpty();
    }
    return recipe.getInputs()[0].getInput();
  }

  public @Nonnull NNList<GrindingBall> getBalls() {
    return balls;
  }

}
