package crazypants.enderio.machine.recipe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.item.ItemStack;

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
  public boolean isInputForRecipe(ItemStack[] test) {
    if(test == null) {
      return false;
    }
    if(test.length != inputs.length) {
      return false;
    }
    List<RecipeInput> recIns = new ArrayList<RecipeInput>(Arrays.asList(inputs));
    for (ItemStack input : test) {
      if(input != null) {
        RecipeInput ri = getInputForStack(input);
        if(ri == null) {
          return false;
        }
        recIns.remove(ri);
      }
    }
    return recIns.size() == 0;
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
