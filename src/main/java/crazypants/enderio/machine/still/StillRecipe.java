package crazypants.enderio.machine.still;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import crazypants.enderio.machine.recipe.IRecipe;
import crazypants.enderio.machine.recipe.RecipeInput;
import crazypants.enderio.machine.recipe.RecipeOutput;

public class StillRecipe implements IRecipe {

  protected final FluidStack inputFluidStack;
  protected final ItemStack[] inputStacks;
  protected final FluidStack outputFluidStack;

  protected final RecipeInput[] inputs;
  protected final RecipeOutput[] output;

  public StillRecipe(FluidStack inputFluid, FluidStack outputFluid, ItemStack... inputStacks) {
    this.inputFluidStack = inputFluid;
    this.inputStacks = inputStacks;
    this.outputFluidStack = outputFluid;

    List<RecipeInput> inputList = new ArrayList<RecipeInput>(3);
    if(inputFluidStack != null) {
      inputList.add(new RecipeInput(inputFluid));
    }
    if(inputStacks != null) {
      for (ItemStack in : inputStacks) {
        if(in != null) {
          inputList.add(new RecipeInput(in));
        }
      }
    }
    inputs = inputList.toArray(new RecipeInput[inputList.size()]);
    output = new RecipeOutput[] { new RecipeOutput(outputFluidStack) };
  }

  @Override
  public boolean isValid() {
    return inputFluidStack != null && inputStacks != null && inputStacks.length > 0 && inputStacks.length < 3 && outputFluidStack != null;
  }

  @Override
  public float getEnergyRequired() {
    return 1000;
  }

  @Override
  public RecipeOutput[] getOutputs() {
    return output;
  }

  @Override
  public RecipeInput[] getInputs() {
    return inputs;
  }

  @Override
  public ItemStack[] getInputStacks() {
    return inputStacks;
  }

  @Override
  public boolean isValidInput(ItemStack item) {
    if(item == null) {
      return false;
    }
    for (RecipeInput ri : inputs) {
      if(item != null && ri.isInput(item)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean isValidInput(FluidStack item) {
    if(item == null) {
      return false;
    }
    for (RecipeInput ri : inputs) {
      if(item.getFluid() != null && item.isFluidEqual(ri.getFluidInput())) {
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean isInputForRecipe(List<ItemStack> test) {
    return false;
  }

  @Override
  public boolean isInputForRecipe(List<ItemStack> test, List<FluidStack> testFluids) {
    if(!isValid() || test == null || test.size() != 2 || testFluids == null || testFluids.size() != 1 || testFluids.get(0) == null) {
      return false;
    }
    boolean validFluid = inputFluidStack.isFluidEqual(testFluids.get(0)) && inputFluidStack.amount <= testFluids.get(0).amount;
    if(!validFluid) {
      return false;
    }

    for (int i = 0; i < inputs.length; i++) {
      if(!containsInput(inputs[i], test)) {
        return false;
      }
    }
    return true;
  }

  private boolean containsInput(RecipeInput recipeInput, List<ItemStack> test) {
    for (ItemStack stack : test) {
      if(recipeInput.isInput(stack)) {
        return true;
      }
    }
    return false;
  }

}
