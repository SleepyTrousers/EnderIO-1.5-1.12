package crazypants.enderio.base.recipe.vat;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.FluidUtil;
import com.enderio.core.common.util.NNList;

import crazypants.enderio.base.Log;
import crazypants.enderio.base.recipe.IRecipe;
import crazypants.enderio.base.recipe.IRecipeInput;
import crazypants.enderio.base.recipe.MachineRecipeInput;
import crazypants.enderio.base.recipe.MachineRecipeRegistry;
import crazypants.enderio.base.recipe.RecipeLevel;
import crazypants.enderio.base.recipe.RecipeOutput;
import crazypants.enderio.util.Prep;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

public class VatRecipeManager {

  static final @Nonnull VatRecipeManager instance = new VatRecipeManager();

  public static @Nonnull VatRecipeManager getInstance() {
    return instance;
  }

  public void create() {
    MachineRecipeRegistry.instance.registerRecipe(MachineRecipeRegistry.VAT, new VatMachineRecipe());
  }

  private final @Nonnull NNList<IRecipe> recipes = new NNList<IRecipe>();

  public IRecipe getRecipeForInput(@Nonnull RecipeLevel machineLevel, @Nonnull NNList<MachineRecipeInput> inputs) {
    if (inputs.size() == 0) {
      return null;
    }
    for (IRecipe recipe : recipes) {
      if (machineLevel.canMake(recipe.getRecipeLevel()) && recipe.isInputForRecipe(inputs)) {
        return recipe;
      }
    }
    return null;
  }

  public void addRecipe(@Nonnull IRecipe recipe) {
    if (!recipe.isValid()) {
      Log.debug("Could not add invalid Vat recipe: " + recipe);
      return;
    }
    recipes.add(new VatRecipe(recipe));
  }

  public @Nonnull NNList<IRecipe> getRecipes() {
    return recipes;
  }

  public boolean isValidInput(@Nonnull RecipeLevel machineLevel, @Nonnull MachineRecipeInput input) {
    for (IRecipe recipe : recipes) {
      if (machineLevel.canMake(recipe.getRecipeLevel())) {
        if (Prep.isValid(input.item) && recipe.isValidInput(input.slotNumber, input.item)) {
          return true;
        } else if (input.fluid != null && recipe.isValidInput(input.fluid)) {
          return true;
        }
      }
    }
    return false;
  }

  public boolean isValidInput(@Nonnull RecipeLevel machineLevel, @Nonnull NNList<MachineRecipeInput> inputs) {
    for (IRecipe recipe : recipes) {
      if (machineLevel.canMake(recipe.getRecipeLevel())) {
        boolean allValid = true;
        for (MachineRecipeInput input : inputs) {
          if (Prep.isValid(input.item)) {
            allValid = recipe.isValidInput(input.slotNumber, input.item);
          } else if (input.fluid != null) {
            allValid = recipe.isValidInput(input.fluid);
          }
          if (!allValid) {
            break;
          }
        }
        if (allValid) {
          return true;
        }
      }
    }
    return false;
  }

  public float getMultiplierForInput(Fluid inputFluid, @Nonnull ItemStack input, Fluid output) {
    if (Prep.isValid(input) || output != null) {
      for (IRecipe recipe : recipes) {
        RecipeOutput out = recipe.getOutputs()[0];
        IRecipeInput in = recipe.getInputs()[recipe.getInputs().length - 1];

        final FluidStack fluidOutput = out.getFluidOutput();
        if ((inputFluid == null || FluidUtil.areFluidsTheSame(in.getFluidInput().getFluid(), inputFluid)
            && (output == null || (fluidOutput != null && FluidUtil.areFluidsTheSame(fluidOutput.getFluid(), output))))) {
          for (IRecipeInput ri : recipe.getInputs()) {
            if (ri.isInput(input)) {
              return ri.getMulitplier();
            }
          }
        }
      }
    }
    // no fluid or not an input for this fluid: best guess
    // (after all, the item IS in the input slot)
    float found = -1f;
    for (IRecipe recipe : recipes) {
      for (IRecipeInput ri : recipe.getInputs()) {
        if (ri.isInput(input)) {
          if (found < 0f || found > ri.getMulitplier()) {
            found = ri.getMulitplier();
          }
        }
      }
    }
    return found > 0 ? found : 0;
  }

}
