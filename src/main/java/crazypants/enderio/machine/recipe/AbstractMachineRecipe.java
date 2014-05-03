package crazypants.enderio.machine.recipe;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import crazypants.enderio.machine.IMachineRecipe;
import crazypants.enderio.machine.MachineRecipeInput;

public abstract class AbstractMachineRecipe implements IMachineRecipe {

  @Override
  public float getEnergyRequired(MachineRecipeInput... inputs) {
    if(inputs == null || inputs.length <= 0) {
      return 0;
    }
    IRecipe recipe = getRecipeForInputs(inputs);
    return recipe == null ? 0 : recipe.getEnergyRequired();
  }

  public abstract IRecipe getRecipeForInputs(MachineRecipeInput[] inputs);

  @Override
  public List<MachineRecipeInput> getQuantitiesConsumed(MachineRecipeInput[] inputs) {
    IRecipe rec = getRecipeForInputs(inputs);
    List<MachineRecipeInput> result = new ArrayList<MachineRecipeInput>();
    for (MachineRecipeInput input : inputs) {
      if(input != null && (input.item != null || input.fluid != null)) {
        for (RecipeInput ri : rec.getInputs()) {
          if(ri.isInput(input.item)) {
            result.add(new MachineRecipeInput(input.slotNumber, ri.getInput().copy()));
          } else if(ri.isInput(input.fluid)) {
            result.add(new MachineRecipeInput(input.slotNumber, ri.getFluidInput().copy()));
          }
        }
      }
    }
    return result;
  }

  @Override
  public float getExperianceForOutput(ItemStack output) {
    return 0;
  }

  @Override
  public boolean isRecipe(MachineRecipeInput... inputs) {
    if(inputs == null || inputs.length <= 0) {
      return false;
    }
    IRecipe recipe = getRecipeForInputs(inputs);
    return recipe != null;
  }

  @Override
  public ResultStack[] getCompletedResult(float chance, MachineRecipeInput... inputs) {
    if(inputs == null || inputs.length <= 0) {
      return new ResultStack[0];
    }
    IRecipe recipe = getRecipeForInputs(inputs);
    if(recipe == null) {
      return new ResultStack[0];
    }
    RecipeOutput[] outputs = recipe.getOutputs();
    if(outputs == null) {
      return new ResultStack[0];
    }
    List<ResultStack> result = new ArrayList<ResultStack>();
    for (RecipeOutput output : outputs) {
      if(output.getChance() >= chance) {
        if(output.isFluid()) {
          result.add(new ResultStack(output.getFluidOutput().copy()));
        } else {
          result.add(new ResultStack(output.getOutput().copy()));
        }
      }
    }
    return result.toArray(new ResultStack[result.size()]);

  }

}
