package crazypants.enderio.base.recipe.basin;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.util.NNList;

import crazypants.enderio.base.Log;
import crazypants.enderio.base.recipe.IRecipe;
import crazypants.enderio.base.recipe.IRecipeInput;
import crazypants.enderio.base.recipe.MachineRecipeInput;
import crazypants.enderio.base.recipe.MachineRecipeRegistry;
import crazypants.enderio.base.recipe.Recipe;
import crazypants.enderio.base.recipe.RecipeInput;
import crazypants.enderio.base.recipe.RecipeOutput;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing.Plane;
import net.minecraftforge.fluids.FluidStack;

public class BasinRecipeManager {

  static final @Nonnull BasinRecipeManager instance = new BasinRecipeManager();

  public static BasinRecipeManager getInstance() {
    return instance;
  }

  private final @Nonnull NNList<BasinRecipe> recipes = new NNList<>();

  public void create() {
    MachineRecipeRegistry.instance.registerRecipe(MachineRecipeRegistry.BASIN, new BasinMachineRecipe());
  }

  public BasinRecipe getRecipeMatchingInput(@Nonnull List<FluidStack> inputs) {
    if (inputs.size() != 4) {
      return null;
    }
    return getRecipeMatchingInput(inputs.get(0), inputs.get(1), inputs.get(2), inputs.get(3));
  }

  public BasinRecipe getRecipeMatchingInput(@Nullable FluidStack inputU, @Nullable FluidStack inputD, @Nullable FluidStack inputL, @Nullable FluidStack inputR) {
    final NNList<MachineRecipeInput> machineRecipeInput = new NNList<>(
        new MachineRecipeInput(0, inputU), new MachineRecipeInput(1, inputD),
        new MachineRecipeInput(2, inputL), new MachineRecipeInput(3, inputR));
    return getRecipeMatchingInput(machineRecipeInput);
  }
  
  public BasinRecipe getRecipeMatchingInput(NNList<MachineRecipeInput> inputs) {
    if (inputs.size() != 4) {
      return null;
    }
    for (BasinRecipe recipe : recipes) {
      if (recipe.isInputForRecipe(inputs)) {
        return recipe;
      }
    }
    return null;
  }
  
  public BasinRecipe getRecipeForInput(@Nonnull FluidStack input, Plane orientation) {
    for (BasinRecipe recipe : recipes) {
      if (recipe.isValidInput(input) && recipe.getOrientation() == orientation) {
        return recipe;
      }
    }
    return null;
  }
  
  public BasinRecipe getRecipeForInput(@Nonnull FluidStack input) {
    for (BasinRecipe recipe : recipes) {
      if (recipe.isValidInput(input)) {
        return recipe;
      }
    }
    return null;
  }

  public boolean isValidInput(MachineRecipeInput input) {
    FluidStack fluid = input.fluid;
    return fluid != null && getRecipeForInput(fluid) != null;
  }

  public void addRecipe(@Nonnull BasinRecipe recipe) {
    if (!recipe.isValid()) {
      Log.debug("Could not add invalid recipe: " + recipe);
      return;
    }
    BasinRecipe rec = getRecipeMatchingInput(getInput(recipe));
    if (rec != null) {
      Log.warn("Not adding supplied recipe as a recipe already exists for the input: " + getInput(recipe));
      return;
    }
    recipes.add(recipe);
  }

  public @Nonnull NNList<? extends Recipe> getRecipes() {
    return recipes;
  }

  public static @Nonnull NNList<FluidStack> getInput(@Nonnull IRecipe recipe) {
    if (recipe.getInputs().length == 0) {
      return NNList.emptyList();
    }
    return recipe.getInputFluidStacks();
  }

  public void addRecipe(@Nonnull FluidStack inputA, @Nonnull FluidStack inputB, @Nonnull ItemStack output, Plane orientation, int energyCost) {
    RecipeOutput recipeOutput = new RecipeOutput(output, 1);
    addRecipe(new BasinRecipe(new RecipeInput(inputA), new RecipeInput(inputB), recipeOutput, orientation, energyCost));
  }

}
