package crazypants.enderio.machine.crusher;

import net.minecraft.item.ItemStack;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.IMachineRecipe;
import crazypants.enderio.machine.RecipeInput;

public class CrusherMachineRecipe implements IMachineRecipe {

  @Override
  public String getUid() {
    return "CrusherRecipe";
  }

  @Override
  public float getEnergyRequired(RecipeInput... inputs) {
    if(inputs == null || inputs.length <= 0) {
      return 0;
    }
    CrusherRecipe recipe = CrusherRecipeManager.instance.getRecipeForInput(inputs[0].item);
    return recipe == null ? 0 : recipe.type.energyCost;
  }

  @Override
  public boolean isRecipe(RecipeInput... inputs) {
    if(inputs == null || inputs.length <= 0) {
      return false;
    }
    CrusherRecipe recipe = CrusherRecipeManager.instance.getRecipeForInput(inputs[0].item);
    return recipe != null;
  }

  @Override
  public ItemStack[] getCompletedResult(RecipeInput... inputs) {
    if(inputs == null || inputs.length <= 0) {
      return new ItemStack[0];
    }
    CrusherRecipe recipe = CrusherRecipeManager.instance.getRecipeForInput(inputs[0].item);
    return recipe == null ? new ItemStack[0] : new ItemStack[] {recipe.output.copy()};
  }

  @Override
  public boolean isValidInput(int slotNumber, ItemStack item) {
    return CrusherRecipeManager.instance.getRecipeForInput(item) != null;
  }

  @Override
  public String getMachineName() {
    return ModObject.blockCrusher.unlocalisedName;
  }

  @Override
  public RecipeInput[] getQuantitiesConsumed(RecipeInput[] inputs) {
    RecipeInput[] res = new RecipeInput[inputs.length];
    int i=0;
    for(RecipeInput input : inputs) {
      ItemStack used = input.item.copy();
      used.stackSize = 1;
      RecipeInput ri = new RecipeInput(input.slotNumber, used);
      res[i] = ri;
      i++;
    }
    return res;
  }

  @Override
  public float getExperianceForOutput(ItemStack output) {
    return 0;
  }

}
