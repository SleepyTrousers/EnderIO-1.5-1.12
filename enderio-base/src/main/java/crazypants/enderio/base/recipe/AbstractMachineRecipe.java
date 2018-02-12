package crazypants.enderio.base.recipe;

import java.util.List;
import java.util.Random;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;

import crazypants.enderio.util.Prep;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public abstract class AbstractMachineRecipe implements IMachineRecipe {

  @Override
  public int getEnergyRequired(@Nonnull NNList<MachineRecipeInput> inputs) {
    if (inputs.size() <= 0) {
      return 0;
    }
    IRecipe recipe = getRecipeForInputs(inputs);
    return recipe == null ? 0 : recipe.getEnergyRequired();
  }

  @Override
  public @Nonnull RecipeBonusType getBonusType(@Nonnull NNList<MachineRecipeInput> inputs) {
    if (inputs.size() <= 0) {
      return RecipeBonusType.NONE;
    }
    IRecipe recipe = getRecipeForInputs(inputs);
    if (recipe == null) {
      return RecipeBonusType.NONE;
    } else {
      return recipe.getBonusType();
    }
  }

  public abstract IRecipe getRecipeForInputs(@Nonnull NNList<MachineRecipeInput> inputs);

  @Override
  public @Nonnull NNList<MachineRecipeInput> getQuantitiesConsumed(@Nonnull NNList<MachineRecipeInput> inputs) {
    IRecipe recipe = getRecipeForInputs(inputs);
    NNList<MachineRecipeInput> result = new NNList<MachineRecipeInput>();

    // Need to make copies so we can reduce their values as we go
    MachineRecipeInput[] availableInputs = new MachineRecipeInput[inputs.size()];
    int i = 0;
    for (MachineRecipeInput available : inputs) {
      availableInputs[i] = available.copy();
      ++i;
    }
    IRecipeInput[] requiredIngredients = new IRecipeInput[recipe.getInputs().length];
    i = 0;
    for (IRecipeInput ri : recipe.getInputs()) {
      requiredIngredients[i] = ri.copy();
      ++i;
    }

    // For each input required by the recipe got through the available machine inputs and consume them
    for (IRecipeInput required : requiredIngredients) {
      for (MachineRecipeInput available : availableInputs) {
        if (required != null && available != null && isValid(available)) {
          if (consume(required, available, result)) {
            break;
          }
        }
      }
    }
    return result;
  }

  protected boolean consume(@Nonnull IRecipeInput required, @Nonnull MachineRecipeInput available, @Nonnull List<MachineRecipeInput> consumedInputs) {

    if (required.isInput(available.fluid)) {
      consumedInputs.add(new MachineRecipeInput(available.slotNumber, required.getFluidInput().copy()));
      return true;
    }

    if (required.isInput(available.item) && (required.getSlotNumber() == -1 || required.getSlotNumber() == available.slotNumber)) {

      ItemStack availableStack = available.item;
      ItemStack requiredStack = required.getInput();

      ItemStack consumedStack = requiredStack.copy();
      consumedStack.setCount(Math.min(requiredStack.getCount(), availableStack.getCount()));

      requiredStack.shrink(consumedStack.getCount());
      availableStack.shrink(consumedStack.getCount());

      consumedInputs.add(new MachineRecipeInput(available.slotNumber, consumedStack));

      if (Prep.isInvalid(requiredStack)) {
        // Fully met the requirement
        return true;
      }

    }
    return false;
  }

  protected boolean isValid(@Nonnull MachineRecipeInput input) {
    if (Prep.isValid(input.item)) {
      return true;
    }
    return input.fluid != null && input.fluid.amount > 0;
  }

  @Override
  public float getExperienceForOutput(@Nonnull ItemStack output) {
    return 0;
  }

  @Override
  public boolean isRecipe(@Nonnull NNList<MachineRecipeInput> inputs) {
    if (inputs.size() <= 0) {
      return false;
    }
    IRecipe recipe = getRecipeForInputs(inputs);
    return recipe != null;
  }

  @Override
  public @Nonnull ResultStack[] getCompletedResult(long nextSeed, float chanceMultiplier, @Nonnull NNList<MachineRecipeInput> inputs) {
    if (inputs.size() <= 0) {
      return new ResultStack[0];
    }
    IRecipe recipe = getRecipeForInputs(inputs);
    if (recipe == null) {
      return new ResultStack[0];
    }
    RecipeOutput[] outputs = recipe.getOutputs();
    if (outputs.length == 0) {
      return new ResultStack[0];
    }
    NNList<ResultStack> result = new NNList<ResultStack>();
    Random rand = new Random(nextSeed);
    for (RecipeOutput output : outputs) {
      if (output.isFluid()) {
        FluidStack fluidOutput = output.getFluidOutput();
        if (fluidOutput != null && (rand.nextFloat() < output.getChance() * chanceMultiplier)) {
          result.add(new ResultStack(fluidOutput = fluidOutput.copy()));
        }
      } else {
        ItemStack stack = output.getOutput().copy();
        int stackSize = 0;
        for (int i = 0; i < stack.getCount(); i++) {
          if (rand.nextFloat() < output.getChance() * chanceMultiplier) {
            stackSize++;
          }
        }
        stack.setCount(stackSize);
        if (Prep.isValid(stack)) {
          result.add(new ResultStack(stack));
        }
      }
    }
    return result.toArray(new ResultStack[0]);
  }

}
