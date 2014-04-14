package crazypants.enderio.machine.still;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import crazypants.enderio.ModObject;
import crazypants.enderio.crafting.IEnderIoRecipe;
import crazypants.enderio.crafting.IRecipeComponent;
import crazypants.enderio.crafting.IRecipeInput;
import crazypants.enderio.crafting.IRecipeOutput;
import crazypants.enderio.crafting.impl.EnderIoRecipe;
import crazypants.enderio.machine.MachineRecipeInput;
import crazypants.enderio.machine.recipe.AbstractMachineRecipe;
import crazypants.enderio.machine.recipe.IRecipe;

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
    if(input == null) {
      return false;
    }
    return VatRecipeManager.instance.isValidInput(input);
  }

  @Override
  public String getMachineName() {
    return ModObject.blockVat.unlocalisedName;
  }

  @Override
  public List<MachineRecipeInput> getQuantitiesConsumed(MachineRecipeInput[] inputs) {

    List<MachineRecipeInput> result = new ArrayList<MachineRecipeInput>();

    VatRecipe rec = (VatRecipe) getRecipeForInputs(inputs);
    FluidStack inputFluidStack = rec.getRequiredFluidInput(inputs);
    result.add(new MachineRecipeInput(0, inputFluidStack));

    for (MachineRecipeInput ri : inputs) {
      if(!ri.isFluid()) {
        ItemStack st = ri.item.copy();
        st.stackSize = rec.getNumConsumed(ri.item);
        result.add(new MachineRecipeInput(ri.slotNumber, st));
      }
    }
    return result;

  }

  @Override
  public ResultStack[] getCompletedResult(float chance, MachineRecipeInput... inputs) {
    if(inputs == null || inputs.length <= 0) {
      return new ResultStack[0];
    }
    VatRecipe recipe = (VatRecipe) getRecipeForInputs(inputs);
    if(recipe == null || !recipe.isValid()) {
      return new ResultStack[0];
    }
    return new ResultStack[] { new ResultStack(recipe.getFluidOutput(inputs)) };
  }

  @Override
  public List<IEnderIoRecipe> getAllRecipes() {
    List<IEnderIoRecipe> result = new ArrayList<IEnderIoRecipe>();
    List<IRecipe> recipes = VatRecipeManager.getInstance().getRecipes();
    for (IRecipe cr : recipes) {
      List<IRecipeComponent> components = new ArrayList<IRecipeComponent>();
      for (crazypants.enderio.machine.recipe.RecipeInput ri : cr.getInputs()) {
        if(ri.getInput() != null) {
          IRecipeInput input = new crazypants.enderio.crafting.impl.RecipeInput(ri.getInput(), ri.getSlotNumber(), ri.getEquivelentInputs());
          components.add(input);
        } else if(ri.getFluidInput() != null) {
          IRecipeInput input = new crazypants.enderio.crafting.impl.RecipeInput(ri.getFluidInput(), 0);
          components.add(input);
        }
      }

      for (crazypants.enderio.machine.recipe.RecipeOutput co : cr.getOutputs()) {
        if(co.isFluid()) {
          IRecipeOutput output = new crazypants.enderio.crafting.impl.RecipeOutput(co.getFluidOutput(), -1);
          components.add(output);
        } else {
          IRecipeOutput output = new crazypants.enderio.crafting.impl.RecipeOutput(co.getOutput(), co.getChance());
          components.add(output);
        }
      }
      result.add(new EnderIoRecipe(IEnderIoRecipe.VAT_ID, cr.getEnergyRequired(), components));
    }
    return result;
  }

}
