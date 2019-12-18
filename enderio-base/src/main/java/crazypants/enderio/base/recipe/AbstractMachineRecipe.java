package crazypants.enderio.base.recipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;

import crazypants.enderio.util.MathUtil;
import crazypants.enderio.util.Prep;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public abstract class AbstractMachineRecipe implements IMachineRecipe {

  private static final @Nonnull ResultStack[] EMPTY_RESULT = new ResultStack[0];

  @Override
  public int getEnergyRequired(@Nonnull NNList<MachineRecipeInput> inputs) {
    if (inputs.size() <= 0) {
      return 0;
    }
    IRecipe recipe = getRecipeForInputs(RecipeLevel.IGNORE, inputs);
    return recipe == null ? 0 : recipe.getEnergyRequired();
  }

  @Override
  public @Nonnull RecipeBonusType getBonusType(@Nonnull NNList<MachineRecipeInput> inputs) {
    if (inputs.size() <= 0) {
      return RecipeBonusType.NONE;
    }
    IRecipe recipe = getRecipeForInputs(RecipeLevel.IGNORE, inputs);
    if (recipe == null) {
      return RecipeBonusType.NONE;
    } else {
      return recipe.getBonusType();
    }
  }

  public abstract IRecipe getRecipeForInputs(@Nonnull RecipeLevel machineLevel, @Nonnull NNList<MachineRecipeInput> inputs);

  @Override
  public @Nonnull NNList<MachineRecipeInput> getQuantitiesConsumed(@Nonnull NNList<MachineRecipeInput> inputs) {
    IRecipe recipe = getRecipeForInputs(RecipeLevel.IGNORE, inputs);
    NNList<MachineRecipeInput> result = new NNList<>();

    // Need to make copies so we can reduce their values as we go
    List<MachineRecipeInput> availableInputs = new ArrayList<>();
    for (MachineRecipeInput available : inputs) {
      availableInputs.add(available.copy());
    }
    List<IRecipeInput> requiredIngredients = new ArrayList<>();
    for (IRecipeInput ri : recipe.getInputs()) {
      requiredIngredients.add(ri.copy());
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

    if (required.isInput(available.item) && MathUtil.isAny(required.getSlotNumber(), -1, available.slotNumber)) {

      ItemStack availableStack = available.item;

      ItemStack consumedStack = availableStack.copy();
      consumedStack.setCount(Math.min(required.getStackSize(), availableStack.getCount()));

      required.shrinkStack(consumedStack.getCount());
      availableStack.shrink(consumedStack.getCount());

      consumedInputs.add(new MachineRecipeInput(available.slotNumber, consumedStack));

      if (required.getStackSize() <= 0) {
        // Fully met the requirement
        return true;
      }

    }
    return false;
  }

  protected boolean isValid(@Nonnull MachineRecipeInput input) {
    return Prep.isValid(input.item) || (input.fluid != null && input.fluid.amount > 0);
  }

  @Override
  public float getExperienceForOutput(@Nonnull ItemStack output) {
    return 0;
  }

  @Override
  public boolean isRecipe(@Nonnull RecipeLevel machineLevel, @Nonnull NNList<MachineRecipeInput> inputs) {
    if (inputs.size() <= 0) {
      return false;
    }
    return getRecipeForInputs(machineLevel, inputs) != null;
  }

  @Override
  public @Nonnull ResultStack[] getCompletedResult(long nextSeed, float chanceMultiplier, @Nonnull NNList<MachineRecipeInput> inputs) {
    if (inputs.size() <= 0) {
      return EMPTY_RESULT;
    }
    IRecipe recipe = getRecipeForInputs(RecipeLevel.IGNORE, inputs);
    if (recipe == null) {
      return EMPTY_RESULT;
    }
    RecipeOutput[] outputs = recipe.getOutputs();
    if (outputs.length == 0) {
      return EMPTY_RESULT;
    }
    NNList<ResultStack> result = new NNList<ResultStack>();
    Random rand = new Random(nextSeed);
    for (RecipeOutput output : outputs) {
      if (output.isFluid()) {
        final FluidStack fluidOutput = output.getFluidOutput();
        if (fluidOutput != null && (rand.nextFloat() < output.getChance() * chanceMultiplier)) {
          result.add(new ResultStack(fluidOutput.copy()));
        }
      } else {
        final ItemStack stack = output.getOutput().copy();
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
    return result.toArray(EMPTY_RESULT);
  }

}
