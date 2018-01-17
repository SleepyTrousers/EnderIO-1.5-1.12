package crazypants.enderio.base.recipe.vat;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;

import crazypants.enderio.base.recipe.AbstractMachineRecipe;
import crazypants.enderio.base.recipe.IRecipe;
import crazypants.enderio.base.recipe.MachineRecipeInput;
import crazypants.enderio.base.recipe.MachineRecipeRegistry;
import crazypants.enderio.util.Prep;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class VatMachineRecipe extends AbstractMachineRecipe {

  @Override
  public @Nonnull String getUid() {
    return "StillRecipe";
  }

  @Override
  public IRecipe getRecipeForInputs(@Nonnull NNList<MachineRecipeInput> inputs) {
    return VatRecipeManager.instance.getRecipeForInput(inputs);
  }

  @Override
  public boolean isValidInput(@Nonnull MachineRecipeInput input) {
    return VatRecipeManager.instance.isValidInput(input);
  }

  @Override
  public @Nonnull String getMachineName() {
    return MachineRecipeRegistry.VAT;
  }

  @Override
  public @Nonnull NNList<MachineRecipeInput> getQuantitiesConsumed(@Nonnull NNList<MachineRecipeInput> inputs) {
    NNList<MachineRecipeInput> result = new NNList<MachineRecipeInput>();

    VatRecipe rec = (VatRecipe) getRecipeForInputs(inputs);
    FluidStack inputFluidStack = rec.getRequiredFluidInput(inputs);
    result.add(new MachineRecipeInput(0, inputFluidStack));

    for (MachineRecipeInput ri : inputs) {
      if (!ri.isFluid() && Prep.isValid(ri.item)) {
        ItemStack st = ri.item.copy();
        st.setCount(rec.getNumConsumed(ri.item));
        result.add(new MachineRecipeInput(ri.slotNumber, st));
      }
    }
    return result;
  }

  @Override
  public @Nonnull ResultStack[] getCompletedResult(float chance, @Nonnull NNList<MachineRecipeInput> inputs) {
    if (inputs.size() <= 0) {
      return new ResultStack[0];
    }
    VatRecipe recipe = (VatRecipe) getRecipeForInputs(inputs);
    if (recipe == null || !recipe.isValid()) {
      return new ResultStack[0];
    }
    final FluidStack fluidOutput = recipe.getFluidOutput(inputs);
    if (fluidOutput == null) {
      return new ResultStack[0];
    }
    return new ResultStack[] { new ResultStack(fluidOutput) };
  }

}
