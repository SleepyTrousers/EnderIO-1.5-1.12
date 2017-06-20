package crazypants.enderio.machine.soul;

import net.minecraft.item.ItemStack;

import java.util.List;

public interface ISoulBinderRecipe {

  ItemStack getInputStack();
  
  ItemStack getOutputStack();
  
  List<String> getSupportedSouls();
  
  int getEnergyRequired();
  
  int getExperienceLevelsRequired();

  int getExperienceRequired();
}
