package crazypants.enderio.base.recipe.sagmill;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;

import crazypants.enderio.base.Log;
import crazypants.enderio.base.recipe.IRecipe;
import crazypants.enderio.base.recipe.MachineRecipeInput;
import crazypants.enderio.base.recipe.MachineRecipeRegistry;
import crazypants.enderio.base.recipe.Recipe;
import crazypants.enderio.util.Prep;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class SagMillRecipeManager {

  public static final int ORE_ENERGY_COST = 400;

  public static final int INGOT_ENERGY_COST = 240;

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

  public boolean isExcludedFromBallBonus(@Nonnull NNList<MachineRecipeInput> inputs) {
    if (inputs.size() < 1) {
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

  public void create() {
    MachineRecipeRegistry.instance.registerRecipe(MachineRecipeRegistry.SAGMILL, new SagMillMachineRecipe());
  }

  public IRecipe getRecipeForInput(@Nonnull ItemStack input) {
    if (Prep.isInvalid(input)) {
      return null;
    }
    final NNList<MachineRecipeInput> machineRecipeInput = new NNList<>(new MachineRecipeInput(0, input));
    for (Recipe recipe : recipes) {
      if (recipe.isInputForRecipe(machineRecipeInput)) {
        return recipe;
      }
    }
    return null;
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

  public void addBall(@Nonnull GrindingBall ball) {
    balls.add(ball);
  }

}
