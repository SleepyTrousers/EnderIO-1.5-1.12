package crazypants.enderio.machine.alloy;

import net.minecraft.item.ItemStack;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.MachineRecipeInput;
import crazypants.enderio.machine.recipe.AbstractMachineRecipe;
import crazypants.enderio.machine.recipe.IRecipe;

public class AlloyMachineRecipe extends AbstractMachineRecipe {

  @Override
  public String getUid() {
    return "AlloySmelterRecipe";
  }

  @Override
  public IRecipe getRecipeForInputs(MachineRecipeInput[] inputs) {
    return AlloyRecipeManager.instance.getRecipeForInputs(inputs);
  }

  @Override
  public boolean isValidInput(MachineRecipeInput input) {
    if(input == null) {
      return false;
    }
    return AlloyRecipeManager.instance.isValidInput(input);
  }

  @Override
  public String getMachineName() {
    return ModObject.blockAlloySmelter.unlocalisedName;
  }

  @Override
  public float getExperianceForOutput(ItemStack output) {
    return AlloyRecipeManager.instance.getExperianceForOutput(output);
  }

  public boolean isValidRecipeComponents(ItemStack[] resultInv) {
    return AlloyRecipeManager.instance.isValidRecipeComponents(resultInv);
  }
}