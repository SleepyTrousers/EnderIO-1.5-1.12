package crazypants.enderio.machine;

import net.minecraft.item.ItemStack;

public interface IMachineRecipe {

  String getUid();
  
  float getEnergyRequired(RecipeInput... inputs);
  
  public boolean isRecipe(RecipeInput... inputs);
  
  ItemStack[] getCompletedResult(RecipeInput... inputs); 
  
  boolean isValidInput(int slotNumber, ItemStack item);
  
  String getMachineName();

  int getQuantityConsumed(RecipeInput input);
  
}
