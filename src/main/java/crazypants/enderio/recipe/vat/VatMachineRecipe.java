package crazypants.enderio.recipe.vat;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import crazypants.enderio.recipe.AbstractMachineRecipe;
import crazypants.enderio.recipe.IRecipe;
import crazypants.enderio.recipe.MachineRecipeInput;
import crazypants.enderio.recipe.MachineRecipeRegistry;
import crazypants.util.Prep;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class VatMachineRecipe extends AbstractMachineRecipe {

  @Override
  public @Nonnull String getUid() {
    return "StillRecipe";
  }

  @Override
  public IRecipe getRecipeForInputs(@Nonnull MachineRecipeInput[] inputs) {
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
  public @Nonnull List<MachineRecipeInput> getQuantitiesConsumed(@Nonnull MachineRecipeInput... inputs) {
    List<MachineRecipeInput> result = new ArrayList<MachineRecipeInput>();

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
  public @Nonnull ResultStack[] getCompletedResult(float chance, @Nonnull MachineRecipeInput... inputs) {
    if (inputs.length <= 0) {
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
