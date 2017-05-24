package crazypants.enderio.recipe;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;

public abstract class AbstractMachineRecipe implements IMachineRecipe {

  @Override
  public int getEnergyRequired(MachineRecipeInput... inputs) {
    if(inputs == null || inputs.length <= 0) {
      return 0;
    }
    IRecipe recipe = getRecipeForInputs(inputs);
    return recipe == null ? 0 : recipe.getEnergyRequired();
  }

  @Override
  public RecipeBonusType getBonusType(MachineRecipeInput... inputs) {
    if(inputs == null || inputs.length <= 0) {
      return RecipeBonusType.NONE;
    }
    IRecipe recipe = getRecipeForInputs(inputs);
    return recipe == null ? RecipeBonusType.NONE : recipe.getBonusType();
  }

  public abstract IRecipe getRecipeForInputs(MachineRecipeInput[] inputs);

  @Override
  public List<MachineRecipeInput> getQuantitiesConsumed(MachineRecipeInput[] inputs) {
    IRecipe recipe = getRecipeForInputs(inputs);
    List<MachineRecipeInput> result = new ArrayList<MachineRecipeInput>();

    //Need to make copies so we can reduce their values as we go
    MachineRecipeInput[] availableInputs = new MachineRecipeInput[inputs.length];
    int i = 0;
    for (MachineRecipeInput available : inputs) {
      availableInputs[i] = available.copy();
      ++i;
    }
    RecipeInput[] requiredIngredients = new RecipeInput[recipe.getInputs().length];
    i = 0;
    for (RecipeInput ri : recipe.getInputs()) {
      requiredIngredients[i] = ri.copy();
      ++i;
    }

    //For each input required by the recipe got through the available machine inputs and consume them
    for (RecipeInput required : requiredIngredients) {
      for (MachineRecipeInput available : availableInputs) {
        if(isValid(available)) {
          if(consume(required, available, result)) {
            break;
          }
        }
      }
    }
    return result;
  }

  protected boolean consume(RecipeInput required, MachineRecipeInput available, List<MachineRecipeInput> consumedInputs) {

    if(required.isInput(available.fluid)) {
      consumedInputs.add(new MachineRecipeInput(available.slotNumber, required.getFluidInput().copy()));
      return true;
    }


    if(required.isInput(available.item) && (required.getSlotNumber() == -1 || required.getSlotNumber() == available.slotNumber)) {

      ItemStack availableStack = available.item;
      ItemStack requiredStack = required.getInput();

      ItemStack consumedStack = requiredStack.copy();
      consumedStack.stackSize = Math.min(requiredStack.stackSize, availableStack.stackSize);

      requiredStack.stackSize -= consumedStack.stackSize;
      availableStack.stackSize -= consumedStack.stackSize;

      consumedInputs.add(new MachineRecipeInput(available.slotNumber, consumedStack));

      if(requiredStack.stackSize <= 0) {
        //Fully met the requirement
        return true;
      }

    }
    return false;
  }

  protected boolean isValid(MachineRecipeInput input) {
    if(input == null) {
      return false;
    }
    if(input.item != null && input.item.stackSize > 0) {
      return true;
    }
    return input.fluid != null && input.fluid.amount > 0;
  }

  @Override
  public float getExperienceForOutput(ItemStack output) {
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
