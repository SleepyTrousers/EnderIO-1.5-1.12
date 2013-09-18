package crazypants.enderio.machine.crusher;

import java.util.ArrayList;
import java.util.List;

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
    return recipe == null ? 0 : recipe.getEnergyRequired();
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
  public ItemStack[] getCompletedResult(float chance, RecipeInput... inputs) {
    if(inputs == null || inputs.length <= 0) {
      return new ItemStack[0];
    }
    CrusherRecipe recipe = CrusherRecipeManager.instance.getRecipeForInput(inputs[0].item);
    if(recipe == null) {
      return new ItemStack[0];
    }
    CrusherOutput[] outputs = recipe.getOutput();
    if(outputs == null) {
      return new ItemStack[0];
    }
    List<ItemStack> result = new ArrayList<ItemStack>();
    for (CrusherOutput output : outputs) {
      if(output.getChance() >= chance) {
        result.add(output.getOutput());
      }
    }
    return result.toArray(new ItemStack[result.size()]);

  }

  @Override
  public boolean isValidInput(RecipeInput input) {
    if(input == null) {
      return false;
    }
    return CrusherRecipeManager.instance.getRecipeForInput(input.item) != null;
  }

  @Override
  public String getMachineName() {
    return ModObject.blockCrusher.unlocalisedName;
  }

  @Override
  public RecipeInput[] getQuantitiesConsumed(RecipeInput[] inputs) {
    RecipeInput[] res = new RecipeInput[inputs.length];
    int i = 0;
    for (RecipeInput input : inputs) {
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
