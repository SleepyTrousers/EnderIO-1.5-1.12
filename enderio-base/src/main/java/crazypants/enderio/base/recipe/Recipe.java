package crazypants.enderio.base.recipe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;

import crazypants.enderio.util.Prep;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class Recipe implements IRecipe {

  private final @Nonnull IRecipeInput[] inputs;
  private final @Nonnull RecipeOutput[] outputs;
  private final int energyRequired;
  private final @Nonnull RecipeBonusType bonusType;

  public Recipe(RecipeOutput output, int energyRequired, @Nonnull RecipeBonusType bonusType, @Nonnull IRecipeInput... input) {
    this(input, new RecipeOutput[] { output }, energyRequired, bonusType);
  }

  public Recipe(IRecipeInput input, int energyRequired, @Nonnull RecipeBonusType bonusType, @Nonnull RecipeOutput... output) {
    this(new IRecipeInput[] { input }, output, energyRequired, bonusType);
  }

  public Recipe(@Nonnull IRecipeInput[] input, @Nonnull RecipeOutput[] output, int energyRequired, @Nonnull RecipeBonusType bonusType) {
    this.inputs = input;
    this.outputs = output;
    this.energyRequired = energyRequired;
    this.bonusType = bonusType;
  }

  @Override
  public boolean isInputForRecipe(NNList<MachineRecipeInput> machineInputs) {
    if (machineInputs == null || machineInputs.size() == 0) {
      return false;
    }

    // fail fast check
    for (MachineRecipeInput realInput : machineInputs) {
      if (realInput != null && (realInput.fluid != null || Prep.isValid(realInput.item)) && !isAnyInput(realInput)) {
        return false;
      }
    }

    List<IRecipeInput> requiredInputs = new ArrayList<>();
    for (IRecipeInput input : inputs) {
      if (input.getFluidInput() != null || Prep.isValid(input.getInput())) {
        requiredInputs.add(input.copy()); // expensive (has ItemStack.copy() inside)
      }
    }

    if (needsExactMatch()) {
      if (requiredInputs.size() != machineInputs.size()) {
        return false;
      }
      for (int i = 0; i < requiredInputs.size(); i++) {
        IRecipeInput input = requiredInputs.get(i);
        MachineRecipeInput machineInput = machineInputs.get(i);
        // If these inputs are not of the same kind, fail
        if (input.isFluid() != machineInput.isFluid()) {
          return false;
        }
        // If the fluids are different, or there is not enough, fail
        if (input.isFluid() && (!input.getFluidInput().isFluidEqual(machineInput.fluid) || input.getFluidInput().amount > machineInput.fluid.amount)) {
          return false;
        // If the items are different, or there is not enough, fail
        } else if (!input.isFluid() && (!input.getInput().isItemEqual(machineInput.item) || input.getInput().getCount() > machineInput.item.getCount())) {
          return false;
        }
      }
      return true;
    }

    for (MachineRecipeInput input : machineInputs) {
      if (input != null && input.isFluid()) {
        Iterator<IRecipeInput> iterator = requiredInputs.iterator();
        while (iterator != null && iterator.hasNext()) {
          IRecipeInput required = iterator.next();
          if (required.isInput(input.fluid)) {
            required.getFluidInput().amount -= input.fluid.amount;
            if (required.getFluidInput().amount <= 0) {
              iterator.remove();
            }
            iterator = null;
          }
        }
        if (iterator != null) {
          // extra input found
          return false;
        }
      }
    }

    for (MachineRecipeInput input : machineInputs) {
      if (input != null && !input.isFluid()) {
        Iterator<IRecipeInput> iterator = requiredInputs.iterator();
        while (iterator != null && iterator.hasNext()) {
          IRecipeInput required = iterator.next();
          if (required.isInput(input.item)) {
            required.shrinkStack(input.item.getCount());
            if (Prep.isInvalid(required.getInput())) {
              iterator.remove();
            }
            iterator = null;
          }
        }
        if (iterator != null) {
          // extra input found
          return false;
        }
      }
    }

    if (!requiredInputs.isEmpty()) {
      // unsatisfied inputs remaining
      return false;
    }
    return true;
  }

  /**
   * If this returns true, the inputs must exactly match that of the recipe. i.e
   * A recipe with two inputs X and Y is only a match when it is checked against
   * exactly two inputs which are identical to X and Y.
   */
  protected boolean needsExactMatch() {
    return false;
  }

  private boolean isAnyInput(@Nonnull MachineRecipeInput realInput) {
    for (IRecipeInput recipeInput : inputs) {
      if (recipeInput != null && ((recipeInput.isInput(realInput.item)) || recipeInput.isInput(realInput.fluid))) {
        return true;
      }
    }
    return false;
  }

  protected int getMinNumInputs() {
    return inputs.length;
  }

  @Override
  public boolean isValidInput(int slot, @Nonnull ItemStack item) {
    return getInputForStack(item) != null;
  }

  @Override
  public boolean isValidInput(@Nonnull FluidStack fluid) {
    return getInputForStack(fluid) != null;
  }

  private IRecipeInput getInputForStack(@Nonnull FluidStack input) {
    for (IRecipeInput ri : inputs) {
      if (ri.isInput(input)) {
        return ri;
      }
    }
    return null;
  }

  private IRecipeInput getInputForStack(@Nonnull ItemStack input) {
    for (IRecipeInput ri : inputs) {
      if (ri.isInput(input)) {
        return ri;
      }
    }
    return null;
  }

  @Override
  public @Nonnull NNList<ItemStack> getInputStacks() {
    NNList<ItemStack> res = new NNList<>();
    for (int i = 0; i < inputs.length; i++) {
      IRecipeInput in = inputs[i];
      if (in != null && !in.isFluid()) {
        final int slotNumber = in.getSlotNumber() >= 0 ? in.getSlotNumber() : i;
        while (res.size() <= slotNumber) {
          res.add(Prep.getEmpty());
        }
        ItemStack input = in.getInput();
        if (Prep.isValid(input)) {
          res.set(slotNumber, input);
        }
      }
    }
    return res;
  }

  @Override
  public @Nonnull NNList<List<ItemStack>> getInputStackAlternatives() {
    NNList<List<ItemStack>> res = new NNList<>();
    for (int i = 0; i < inputs.length; i++) {
      IRecipeInput in = inputs[i];
      if (in != null && !in.isFluid()) {
        final int slotNumber = in.getSlotNumber() >= 0 ? in.getSlotNumber() : i;
        while (res.size() <= slotNumber) {
          res.add(new NNList<>());
        }
        ItemStack[] equivelentInputs = in.getEquivelentInputs();
        if (equivelentInputs != null && equivelentInputs.length != 0) {
          ((NNList<ItemStack>) res.get(slotNumber)).addAll(equivelentInputs);
        } else {
          ItemStack input = in.getInput();
          if (Prep.isValid(input)) {
            ((NNList<ItemStack>) res.get(slotNumber)).add(input);
          }
        }
      }
    }
    return res;
  }

  @Override
  public NNList<FluidStack> getInputFluidStacks() {
    NNList<FluidStack> res = new NNList<FluidStack>();
    for (int i = 0; i < inputs.length; i++) {
      IRecipeInput in = inputs[i];
      if (in != null && in.getFluidInput() != null) {
        res.add(in.getFluidInput());
      }
    }
    return res;
  }

  @Override
  public @Nonnull IRecipeInput[] getInputs() {
    return inputs;
  }

  @Override
  public @Nonnull RecipeOutput[] getOutputs() {
    return outputs;
  }

  @Override
  public @Nonnull RecipeBonusType getBonusType() {
    return bonusType;
  }

  public boolean hasOuput(@Nonnull ItemStack result) {
    if (Prep.isInvalid(result)) {
      return false;
    }
    for (RecipeOutput output : outputs) {
      ItemStack os = output.getOutput();
      if (os.isItemEqual(result)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public int getEnergyRequired() {
    return energyRequired;
  }

  @Override
  public boolean isValid() {
    if (energyRequired <= 0) {
      return false;
    }
    for (IRecipeInput input : inputs) {
      if (!input.isValid()) {
        return false;
      }
    }
    for (RecipeOutput output : outputs) {
      if (!output.isValid()) {
        return false;
      }
    }
    return true;
  }

  @Override
  public String toString() {
    return "Recipe [input=" + Arrays.toString(inputs) + ", output=" + Arrays.toString(outputs) + ", energyRequired=" + energyRequired + "]";
  }

  @Override
  public boolean isSynthetic() {
    return false;
  }

}
