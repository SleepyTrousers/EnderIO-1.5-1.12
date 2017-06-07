package crazypants.enderio.recipe;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.recipe.sagmill.SagMillRecipeManager;
import crazypants.util.Prep;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public abstract class AbstractMachineRecipe implements IMachineRecipe {

  @Override
  public int getEnergyRequired(@Nonnull MachineRecipeInput... inputs) {
    if (inputs.length <= 0) {
      return 0;
    }
    IRecipe recipe = getRecipeForInputs(inputs);
    return recipe == null ? 0 : recipe.getEnergyRequired();
  }

  @Override
  public @Nonnull RecipeBonusType getBonusType(@Nonnull MachineRecipeInput... inputs) {
    if (inputs.length <= 0) {
      return RecipeBonusType.NONE;
    }
    IRecipe recipe = getRecipeForInputs(inputs);
    if (recipe == null) {
      return RecipeBonusType.NONE;
    } else {
      return recipe.getBonusType().withoutMultiply(SagMillRecipeManager.getInstance().isExcludedFromBallBonus(inputs));
    }
  }

  public abstract IRecipe getRecipeForInputs(@Nonnull MachineRecipeInput[] inputs);

  @Override
  public @Nonnull List<MachineRecipeInput> getQuantitiesConsumed(@Nonnull MachineRecipeInput... inputs) {
    IRecipe recipe = getRecipeForInputs(inputs);
    List<MachineRecipeInput> result = new ArrayList<MachineRecipeInput>();

    // Need to make copies so we can reduce their values as we go
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

    // For each input required by the recipe got through the available machine inputs and consume them
    for (RecipeInput required : requiredIngredients) {
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

  protected boolean consume(@Nonnull RecipeInput required, @Nonnull MachineRecipeInput available, @Nonnull List<MachineRecipeInput> consumedInputs) {

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
  public boolean isRecipe(@Nonnull MachineRecipeInput... inputs) {
    if (inputs.length <= 0) {
      return false;
    }
    IRecipe recipe = getRecipeForInputs(inputs);
    return recipe != null;
  }

  @Override
  public @Nonnull ResultStack[] getCompletedResult(float chance, @Nonnull MachineRecipeInput... inputs) {
    if (inputs.length <= 0) {
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
    for (RecipeOutput output : outputs) {
      if (output.getChance() >= chance) {
        final FluidStack fluidOutput = output.getFluidOutput();
        if (output.isFluid() && fluidOutput != null) {
          result.add(new ResultStack(NullHelper.notnullF(fluidOutput.copy(), "FluidStack.copy()")));
        } else {
          result.add(new ResultStack(output.getOutput().copy()));
        }
      }
    }
    return result.toArray();

  }

}
