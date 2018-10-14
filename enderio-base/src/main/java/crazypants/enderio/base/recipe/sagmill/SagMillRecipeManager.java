package crazypants.enderio.base.recipe.sagmill;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;

import crazypants.enderio.base.Log;
import crazypants.enderio.base.recipe.IRecipe;
import crazypants.enderio.base.recipe.MachineRecipeInput;
import crazypants.enderio.base.recipe.MachineRecipeRegistry;
import crazypants.enderio.base.recipe.Recipe;
import crazypants.enderio.base.recipe.RecipeLevel;
import crazypants.enderio.util.Prep;
import net.minecraft.item.ItemStack;

public final class SagMillRecipeManager {

  public static final int ORE_ENERGY_COST = 400;

  public static final int INGOT_ENERGY_COST = 240;

  static final @Nonnull SagMillRecipeManager instance = new SagMillRecipeManager();

  public static @Nonnull SagMillRecipeManager getInstance() {
    return instance;
  }

  private final @Nonnull NNList<Recipe> recipes = new NNList<Recipe>();

  private final @Nonnull NNList<GrindingBall> balls = new NNList<GrindingBall>();

  private SagMillRecipeManager() {
  }

  public boolean isValidSagBall(@Nonnull ItemStack stack) {
    return getGrindballFromStack(stack) != null;
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

  public boolean isValidInput(@Nonnull RecipeLevel machineLevel, @Nonnull MachineRecipeInput input) {
    if (input.slotNumber == 1) {
      return isValidSagBall(input.item);
    }
    return getRecipeForInput(machineLevel, input.item) != null;
  }

  public void create() {
    MachineRecipeRegistry.instance.registerRecipe(MachineRecipeRegistry.SAGMILL, new SagMillMachineRecipe());
  }

  public IRecipe getRecipeForInput(@Nonnull RecipeLevel machineLevel, @Nonnull ItemStack input) {
    if (Prep.isInvalid(input)) {
      return null;
    }
    final NNList<MachineRecipeInput> machineRecipeInput = new NNList<>(new MachineRecipeInput(0, input));
    for (Recipe recipe : recipes) {
      if (machineLevel.canMake(recipe.getRecipeLevel()) && recipe.isInputForRecipe(machineRecipeInput)) {
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
    IRecipe rec = getRecipeForInput(RecipeLevel.IGNORE, getInput(recipe));
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
