package crazypants.enderio.machine.soul;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public interface ISoulBinderRecipe {

  ItemStack getInputStack();
  
  ItemStack getOutputStack();
  
  List<ResourceLocation> getSupportedSouls();
  
  int getEnergyRequired();
  
  int getExperienceLevelsRequired();

  int getExperienceRequired();
}
