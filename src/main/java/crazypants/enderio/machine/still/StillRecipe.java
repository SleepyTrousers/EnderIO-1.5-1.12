package crazypants.enderio.machine.still;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import crazypants.enderio.machine.recipe.IRecipe;
import crazypants.enderio.machine.recipe.Recipe;
import crazypants.enderio.machine.recipe.RecipeInput;
import crazypants.enderio.machine.recipe.RecipeOutput;

public class StillRecipe implements IRecipe {

  protected final FluidStack inputFluidStack;
  protected final List<ItemStack> inputStacks;
  protected final FluidStack outputFluidStack;

  protected final RecipeInput[] inputs;
  protected final RecipeOutput[] output;
  protected final float energyRequired;

  //  public StillRecipe(FluidStack inputFluid, FluidStack outputFluid, ItemStack... inputStacks) {
  //    this.inputFluidStack = inputFluid;
  //    this.inputStacks = inputStacks;
  //    this.outputFluidStack = outputFluid;
  //
  //    List<RecipeInput> inputList = new ArrayList<RecipeInput>(3);
  //    if(inputFluidStack != null) {
  //      inputList.add(new RecipeInput(inputFluid));
  //    }
  //    if(inputStacks != null) {
  //      for (ItemStack in : inputStacks) {
  //        if(in != null) {
  //          inputList.add(new RecipeInput(in));
  //        }
  //      }
  //    }
  //    inputs = inputList.toArray(new RecipeInput[inputList.size()]);
  //    output = new RecipeOutput[] { new RecipeOutput(outputFluidStack) };
  //  }

  public StillRecipe(Recipe recipe) {
    List<FluidStack> fluids = recipe.getInputFluidStacks();
    if(fluids != null && !fluids.isEmpty()) {
      inputFluidStack = fluids.get(0).copy();
    } else {
      inputFluidStack = null;
    }
    this.inputStacks = recipe.getInputStacks();

    FluidStack os = null;
    for (RecipeOutput output : recipe.getOutputs()) {
      if(output.isFluid()) {
        os = output.getFluidOutput().copy();
        break;
      }
    }
    outputFluidStack = os;

    List<RecipeInput> inputList = new ArrayList<RecipeInput>(3);
    if(inputFluidStack != null) {
      inputList.add(new RecipeInput(inputFluidStack));
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

    energyRequired = recipe.getEnergyRequired();

    float inputFluidMul = 1;
    float outputFluidMul = 1;
    for (RecipeInput ri : recipe.getInputs()) {
      if(!ri.isFluid()) {
        inputFluidMul *= ri.getMulitplier();
      }
      outputFluidMul *= ri.getMulitplier();
    }
    inputFluidStack.amount = Math.round(inputFluidMul * FluidContainerRegistry.BUCKET_VOLUME);
    outputFluidStack.amount = Math.round(outputFluidMul * FluidContainerRegistry.BUCKET_VOLUME);

    System.out.println("StillRecipe.StillRecipe: Input amount = " + inputFluidStack.amount);
    System.out.println("StillRecipe.StillRecipe: Output amount = " + outputFluidStack.amount);

  }

  @Override
  public boolean isValid() {
    return inputFluidStack != null && inputStacks != null && !inputStacks.isEmpty() && inputStacks.size() < 3 && outputFluidStack != null;
  }

  @Override
  public float getEnergyRequired() {
    return energyRequired;
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
  public List<ItemStack> getInputStacks() {
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
      if(!inputs[i].isFluid() && !containsInput(inputs[i], test)) {
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

  @Override
  public List<FluidStack> getInputFluidStacks() {
    return Collections.singletonList(inputFluidStack);
  }

}
