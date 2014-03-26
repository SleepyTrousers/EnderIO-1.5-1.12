package crazypants.enderio.machine.recipe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class Recipe implements IRecipe {

  private final RecipeInput[] inputs;
  private final RecipeOutput[] outputs;
  private final float energyRequired;

  public Recipe(RecipeOutput output, float energyRequired, RecipeInput... input) {
    this(input, new RecipeOutput[] { output }, energyRequired);
  }

  public Recipe(RecipeInput input, float energyRequired, RecipeOutput... output) {
    this(new RecipeInput[] { input }, output, energyRequired);
  }

  public Recipe(RecipeInput[] input, RecipeOutput[] output, float energyRequired) {
    this.inputs = input;
    this.outputs = output;
    this.energyRequired = energyRequired;
  }

  @Override
  public boolean isInputForRecipe(List<ItemStack> test) {
    return isInputForRecipe(test, null);
  }

  @Override
  public boolean isInputForRecipe(List<ItemStack> test, List<FluidStack> testFluids) {
    if(test == null) {
      return false;
    }
    if(test.size() != inputs.length) {
      return false;
    }
    List<RecipeInput> recIns = new ArrayList<RecipeInput>(Arrays.asList(inputs));
    for (ItemStack input : test) {
      if(input != null) {
        RecipeInput ri = getInputForStack(input);
        if(ri == null || ri.getInput() == null) {
          return false;
        }
        if(input.stackSize < ri.getInput().stackSize) {
          return false;
        }
        recIns.remove(ri);
      }
    }
    if(testFluids != null) {
      for (FluidStack input : testFluids) {
        if(input != null) {
          RecipeInput ri = getInputForStack(input);
          if(ri == null || ri.getInput() == null) {
            return false;
          }
          if(input.amount < ri.getFluidInput().amount) {
            return false;
          }
          recIns.remove(ri);
        }
      }
    }
    return recIns.size() == 0;
  }

  @Override
  public boolean isValidInput(ItemStack item) {
    return getInputForStack(item) != null;
  }

  @Override
  public boolean isValidInput(FluidStack fluid) {
    return getInputForStack(fluid) != null;
  }

  private RecipeInput getInputForStack(FluidStack input) {
    for (RecipeInput ri : inputs) {
      if(ri.isInput(input)) {
        return ri;
      }
    }
    return null;
  }

  private RecipeInput getInputForStack(ItemStack input) {
    for (RecipeInput ri : inputs) {
      if(ri.isInput(input)) {
        return ri;
      }
    }
    return null;
  }

  @Override
  public ItemStack[] getInputStacks() {
    if(inputs == null) {
      return new ItemStack[0];
    }
    ItemStack[] res = new ItemStack[inputs.length];
    for (int i = 0; i < res.length; i++) {
      RecipeInput in = inputs[i];
      if(in != null) {
        res[i] = in.getInput();
      }
    }
    return res;
  }

  @Override
  public RecipeInput[] getInputs() {
    return inputs;
  }

  @Override
  public RecipeOutput[] getOutputs() {
    return outputs;
  }

  @Override
  public float getEnergyRequired() {
    return energyRequired;
  }

  @Override
  public boolean isValid() {
    return inputs != null && outputs != null && energyRequired > 0;
  }

  @Override
  public String toString() {
    return "Recipe [input=" + Arrays.toString(inputs) + ", output=" + Arrays.toString(outputs) + ", energyRequired=" + energyRequired + "]";
  }

}
