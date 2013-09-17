package crazypants.enderio.machine;

import net.minecraft.item.ItemStack;

public interface IMachineRecipe {

  String getUid();

  float getEnergyRequired(RecipeInput... inputs);

  public boolean isRecipe(RecipeInput... inputs);

  ItemStack[] getCompletedResult(RecipeInput... inputs);
  
  float getExperianceForOutput(ItemStack output);

  boolean isValidInput(int slotNumber, ItemStack item);

  String getMachineName();

  RecipeInput[] getQuantitiesConsumed(RecipeInput[] inputs);

}
