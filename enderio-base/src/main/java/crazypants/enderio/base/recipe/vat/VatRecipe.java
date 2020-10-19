package crazypants.enderio.base.recipe.vat;

import java.util.List;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NullHelper;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import crazypants.enderio.base.Log;
import crazypants.enderio.base.recipe.IRecipe;
import crazypants.enderio.base.recipe.IRecipeInput;
import crazypants.enderio.base.recipe.MachineRecipeInput;
import crazypants.enderio.base.recipe.RecipeBonusType;
import crazypants.enderio.base.recipe.RecipeLevel;
import crazypants.enderio.base.recipe.RecipeOutput;
import crazypants.enderio.util.Prep;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public class VatRecipe implements IRecipe {

  protected final @Nonnull NNList<ItemStack> inputStacks;
  private final @Nonnull NNList<List<ItemStack>> inputStackAlternatives;
  protected final boolean valid;

  protected final @Nonnull Table<IRecipeInput, IRecipeInput, FluidStack> inputFluidStacks = NullHelper.notnull(HashBasedTable.create(),
      "HashBasedTable.create()");
  protected final @Nonnull Table<IRecipeInput, IRecipeInput, FluidStack> outputFluidStacks = NullHelper.notnull(HashBasedTable.create(),
      "HashBasedTable.create()");

  protected final @Nonnull IRecipeInput[] inputs;
  protected final @Nonnull RecipeOutput[] output;
  protected final int energyRequired;
  private int requiredItems;
  private final @Nonnull RecipeLevel recipeLevel;

  public VatRecipe(@Nonnull IRecipe recipe) {
    FluidStack inputFluidStack = null, outputFluidStack = null;

    inputs = recipe.getInputs();
    inputStackAlternatives = recipe.getInputStackAlternatives();

    for (RecipeOutput recipeOutput : recipe.getOutputs()) {
      final FluidStack fluidOutput = recipeOutput.getFluidOutput();
      if (recipeOutput.isFluid() && fluidOutput != null) {
        outputFluidStack = fluidOutput.copy();
        break;
      }
    }

    if (outputFluidStack == null) {
      Log.warn("Ignoring invalid VAT recipe without output fluid");
      output = new RecipeOutput[0];
    } else {

      requiredItems = 1;
      for (IRecipeInput ri : inputs) {
        if (!ri.isFluid() && ri.getSlotNumber() == 1) {
          requiredItems = 2;
        }
      }

      if (requiredItems == 2) {
        for (IRecipeInput r0 : inputs) {
          if (!r0.isFluid() && r0.getSlotNumber() == 0) {
            for (IRecipeInput r1 : inputs) {
              if (!r1.isFluid() && r1.getSlotNumber() == 1) {
                for (IRecipeInput r2 : inputs) {
                  if (r2.isFluid()) {
                    float im = r0.getMulitplier() * r1.getMulitplier();
                    inputFluidStack = r2.getFluidInput().copy();
                    inputFluidStack.amount = Math.round(Fluid.BUCKET_VOLUME * im);
                    outputFluidStack.amount = Math.round(im * r2.getMulitplier() * Fluid.BUCKET_VOLUME);
                    inputFluidStacks.put(r0, r1, inputFluidStack.copy());
                    outputFluidStacks.put(r0, r1, outputFluidStack.copy());
                    // TODO: Mod ee3
                    // registerRecipe(outputFluidStack.copy(), r0.getInput().copy(), r1.getInput().copy(), inputFluidStack.copy());
                  }
                }
              }
            }
          }
        }
      } else {
        for (IRecipeInput r0 : inputs) {
          if (!r0.isFluid()) {
            for (IRecipeInput r2 : inputs) {
              if (r2.isFluid()) {
                float im = r0.getMulitplier();
                inputFluidStack = r2.getFluidInput().copy();
                inputFluidStack.amount = Math.round(Fluid.BUCKET_VOLUME * im);
                outputFluidStack.amount = Math.round(im * r2.getMulitplier() * Fluid.BUCKET_VOLUME);
                inputFluidStacks.put(r0, r0, inputFluidStack.copy());
                outputFluidStacks.put(r0, r0, outputFluidStack.copy());
                // TODO: Mod ee3
                // registerRecipe(outputFluidStack.copy(), r0.getInput().copy(), inputFluidStack.copy());
              }
            }
          }
        }
      }

      if (inputFluidStack == null) {
        Log.warn("Ignoring invalid VAT recipe without input fluid/stacks");
      }
      output = new RecipeOutput[] { new RecipeOutput(outputFluidStack) };
    }

    energyRequired = recipe.getEnergyRequired();
    recipeLevel = recipe.getRecipeLevel();

    this.inputStacks = recipe.getInputStacks();
    valid = inputFluidStack != null && !inputStacks.isEmpty() && inputStacks.size() > 0 && outputFluidStack != null;
  }

  @Override
  public boolean isValid() {
    return valid;
  }

  @Override
  public int getEnergyRequired() {
    return energyRequired;
  }

  @Override
  public @Nonnull RecipeOutput[] getOutputs() {
    return output;
  }

  @Override
  public @Nonnull IRecipeInput[] getInputs() {
    return inputs;
  }

  @Override
  public @Nonnull NNList<ItemStack> getInputStacks() {
    return inputStacks;
  }

  @Override
  public @Nonnull RecipeBonusType getBonusType() {
    return RecipeBonusType.NONE;
  }

  private IRecipeInput getRecipeInput(int slot, ItemStack item) {
    if (item == null) {
      return null;
    }
    for (IRecipeInput ri : inputs) {
      if (ri.getSlotNumber() == slot && ri.isInput(item)) {
        return ri;
      }
    }
    return null;
  }

  @Override
  public boolean isValidInput(int slot, @Nonnull ItemStack item) {
    return getRecipeInput(slot, item) != null;
  }

  @Override
  public boolean isValidInput(@Nonnull FluidStack item) {
    for (IRecipeInput ri : inputs) {
      if (item.getFluid() != null && item.isFluidEqual(ri.getFluidInput())) {
        return true;
      }
    }
    return false;
  }

  public static class RecipeMatch {
    public IRecipeInput r0, r1;
    public FluidStack in, out;

    void setRecipeInput(IRecipeInput r) {
      if (r != null) {
        if (r.getSlotNumber() == 0) {
          r0 = r;
        } else {
          r1 = r;
        }
      }
    }
  }

  private RecipeMatch matchRecipe(NNList<MachineRecipeInput> recipeInputs) {
    if (!isValid() || recipeInputs == null || recipeInputs.size() < requiredItems + 1) {
      return null;
    }
    FluidStack inputFluid = null;
    RecipeMatch m = new RecipeMatch();
    for (MachineRecipeInput in : recipeInputs) {
      if (in.isFluid()) {
        inputFluid = in.fluid;
      } else {
        m.setRecipeInput(getRecipeInput(in.slotNumber, in.item));
      }
    }
    if (requiredItems == 1) {
      m.r1 = m.r0;
    }
    m.in = inputFluidStacks.get(m.r0, m.r1);
    m.out = outputFluidStacks.get(m.r0, m.r1);
    if (inputFluid != null && inputFluid.containsFluid(m.in)) {
      return m;
    } else {
      return null;
    }
  }

  public RecipeMatch matchRecipe(@Nonnull FluidStack inputFluid, @Nonnull ItemStack in0, @Nonnull ItemStack in1) {
    if (!isValid() || (requiredItems == 1) != Prep.isInvalid(in1)) {
      return null;
    }
    RecipeMatch m = new RecipeMatch();
    m.setRecipeInput(getRecipeInput(0, in0));
    if (requiredItems == 1) {
      m.r1 = m.r0;
    } else {
      m.setRecipeInput(getRecipeInput(1, in1));
    }
    m.in = inputFluidStacks.get(m.r0, m.r1);
    m.out = outputFluidStacks.get(m.r0, m.r1);
    return m;
  }

  @Override
  public boolean isInputForRecipe(NNList<MachineRecipeInput> recipeInputs) {
    RecipeMatch m = matchRecipe(recipeInputs);
    return m != null;
  }

  @Override
  public NNList<FluidStack> getInputFluidStacks() {
    for (IRecipeInput r2 : inputs) {
      if (r2.isFluid()) {
        return new NNList<>(r2.getFluidInput().copy());
      }
    }
    return NNList.emptyList();
  }

  public float getMultiplierForInput(FluidStack item) {
    for (IRecipeInput input : inputs) {
      if (input.isInput(item)) {
        return input.getMulitplier();
      }
    }
    return 1;
  }

  public FluidStack getRequiredFluidInput(NNList<MachineRecipeInput> recipeInputs) {
    RecipeMatch m = matchRecipe(recipeInputs);
    if (m != null) {
      return m.in;
    } else {
      // inputs are no valid recipe.
      return new FluidStack(FluidRegistry.WATER, Fluid.BUCKET_VOLUME * 999);
    }
  }

  public FluidStack getFluidOutput(NNList<MachineRecipeInput> recipeInputs) {
    RecipeMatch m = matchRecipe(recipeInputs);
    if (m != null) {
      return m.out;
    } else {
      // inputs are no valid recipe.
      return new FluidStack(FluidRegistry.WATER, 0);
    }
  }

  public int getNumConsumed(ItemStack item) {
    for (IRecipeInput input : inputs) {
      if (item != null && input.isInput(item)) {
        return input.getStackSize();
      }
    }
    return 1;
  }

  @Override
  public @Nonnull NNList<List<ItemStack>> getInputStackAlternatives() {
    return inputStackAlternatives;
  }

  @Override
  public boolean isSynthetic() {
    return false;
  }

  @Override
  @Nonnull
  public RecipeLevel getRecipeLevel() {
    return recipeLevel;
  }

}
