package crazypants.enderio.recipe.soul;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public interface ISoulBinderRecipe {

  @Nonnull
  ItemStack getInputStack();

  @Nonnull
  ItemStack getOutputStack();

  NNList<ResourceLocation> getSupportedSouls();

  int getEnergyRequired();

  int getExperienceLevelsRequired();

  int getExperienceRequired();

}
