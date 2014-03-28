package crazypants.enderio.machine.recipe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import crazypants.enderio.machine.MachineRecipeInput;

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
  public boolean isInputForRecipe(MachineRecipeInput... inputs) {
    if(inputs == null || inputs.length == 0) {
      return false;
    }

    int usedCount = 0;
    for (MachineRecipeInput input : inputs) {
      if(input != null) {
        if(input.item != null) {
          RecipeInput ri = getInputForStack(input.item);
          if(ri == null || ri.getInput() == null) {
            return false;
          }
          if(input.item.stackSize < ri.getInput().stackSize) {
            return false;
          }
          usedCount++;
        } else if(input.fluid != null) {
          RecipeInput ri = getInputForStack(input.fluid);
          if(ri == null || ri.getInput() == null) {
            return false;
          }
          if(input.fluid.amount < ri.getFluidInput().amount) {
            return false;
          }
          usedCount++;
        }
      }
    }
    return usedCount == getMinNumInputs();
  }

  protected int getMinNumInputs() {
    return inputs.length;
  }

  @Override
  public boolean isValidInput(int slot, ItemStack item) {
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
  public List<ItemStack> getInputStacks() {
    if(inputs == null) {
      return Collections.emptyList();
    }
    List<ItemStack> res = new ArrayList<ItemStack>(inputs.length);
    for (int i = 0; i < inputs.length; i++) {
      RecipeInput in = inputs[i];
      if(in != null && in.getInput() != null) {
        res.add(in.getInput());
      }
    }
    return res;
  }

  @Override
  public List<FluidStack> getInputFluidStacks() {
    if(inputs == null) {
      return Collections.emptyList();
    }
    List<FluidStack> res = new ArrayList<FluidStack>(inputs.length);
    for (int i = 0; i < inputs.length; i++) {
      RecipeInput in = inputs[i];
      if(in != null && in.getFluidInput() != null) {
        res.add(in.getFluidInput());
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
