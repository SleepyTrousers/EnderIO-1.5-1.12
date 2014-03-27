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
import crazypants.enderio.crafting.impl.RecipeInput;
import crazypants.enderio.machine.MachineRecipeInput;
import crazypants.enderio.machine.recipe.AbstractMachineRecipe;
import crazypants.enderio.machine.recipe.IRecipe;

public class StillMachineRecipe extends AbstractMachineRecipe {

  @Override
  public String getUid() {
    return "StillRecipe";
  }

  @Override
  public IRecipe getRecipeForInputs(MachineRecipeInput[] inputs) {
    List<ItemStack> items = new ArrayList<ItemStack>();
    List<FluidStack> fluids = new ArrayList<FluidStack>();
    for (MachineRecipeInput mi : inputs) {
      if(mi != null) {
        if(mi.item != null) {
          items.add(mi.item);
        } else if(mi.fluid != null) {
          fluids.add(mi.fluid);
        }
      }
    }
    return StillRecipeManager.instance.getRecipeForInput(items, fluids);
  }

  @Override
  public boolean isValidInput(MachineRecipeInput input) {
    if(input == null) {
      return false;
    }
    return StillRecipeManager.instance.isValidInput(input);
  }

  @Override
  public String getMachineName() {
    return ModObject.blockStill.unlocalisedName;
  }

  @Override
  public List<IEnderIoRecipe> getAllRecipes() {
    List<IEnderIoRecipe> result = new ArrayList<IEnderIoRecipe>();
    List<IRecipe> recipes = StillRecipeManager.getInstance().getRecipes();
    for (IRecipe cr : recipes) {
      List<IRecipeComponent> components = new ArrayList<IRecipeComponent>();
      for (crazypants.enderio.machine.recipe.RecipeInput ri : cr.getInputs()) {
        if(ri.getInput() != null) {
          IRecipeInput input = new RecipeInput(ri.getInput(), -1, ri.getEquivelentInputs());
          components.add(input);
        }
      }

      for (crazypants.enderio.machine.recipe.RecipeOutput co : cr.getOutputs()) {
        IRecipeOutput output = new crazypants.enderio.crafting.impl.RecipeOutput(co.getOutput(), co.getChance());
        components.add(output);
      }
      result.add(new EnderIoRecipe(IEnderIoRecipe.STILL_ID, cr.getEnergyRequired(), components));
    }
    return result;
  }

}
