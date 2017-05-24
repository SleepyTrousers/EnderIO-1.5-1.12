package crazypants.enderio.recipe.vat;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import crazypants.enderio.ModObject;
import crazypants.enderio.recipe.AbstractMachineRecipe;
import crazypants.enderio.recipe.IRecipe;
import crazypants.enderio.recipe.MachineRecipeInput;

public class VatMachineRecipe extends AbstractMachineRecipe {

  @Override
  public String getUid() {
    return "StillRecipe";
  }

  @Override
  public IRecipe getRecipeForInputs(MachineRecipeInput[] inputs) {
    return VatRecipeManager.instance.getRecipeForInput(inputs);
  }

  @Override
  public boolean isValidInput(MachineRecipeInput input) {
    if (input == null) {
      return false;
    }
    return VatRecipeManager.instance.isValidInput(input);
  }

  @Override
  public String getMachineName() {
    return ModObject.blockVat.getUnlocalisedName();
  }

  @Override
  public List<MachineRecipeInput> getQuantitiesConsumed(MachineRecipeInput[] inputs) {
    List<MachineRecipeInput> result = new ArrayList<MachineRecipeInput>();

    VatRecipe rec = (VatRecipe) getRecipeForInputs(inputs);
    FluidStack inputFluidStack = rec.getRequiredFluidInput(inputs);
    result.add(new MachineRecipeInput(0, inputFluidStack));

    for (MachineRecipeInput ri : inputs) {
      if (!ri.isFluid() && ri.item != null) {
        ItemStack st = ri.item.copy();
        st.stackSize = rec.getNumConsumed(ri.item);
        result.add(new MachineRecipeInput(ri.slotNumber, st));
      }
    }
    return result;
  }

  @Override
  public ResultStack[] getCompletedResult(float chance, MachineRecipeInput... inputs) {
    if (inputs == null || inputs.length <= 0) {
      return new ResultStack[0];
    }
    VatRecipe recipe = (VatRecipe) getRecipeForInputs(inputs);
    if (recipe == null || !recipe.isValid()) {
      return new ResultStack[0];
    }
    return new ResultStack[] { new ResultStack(recipe.getFluidOutput(inputs)) };
  }

}
