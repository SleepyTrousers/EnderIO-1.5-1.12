package crazypants.enderio.base.recipe.soul;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;

import crazypants.enderio.base.recipe.IMachineRecipe;
import crazypants.enderio.base.xp.XpUtil;
import crazypants.enderio.util.CapturedMob;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public interface ISoulBinderRecipe extends IMachineRecipe {

  @Nonnull
  ItemStack getInputStack();

  @Nonnull
  ItemStack getOutputStack();

  NNList<ResourceLocation> getSupportedSouls();

  int getEnergyRequired();

  int getExperienceLevelsRequired();

  default int getExperienceRequired() {
    return XpUtil.getExperienceForLevel(getExperienceLevelsRequired());
  }

  @Nonnull
  ItemStack getOutputStack(@Nonnull ItemStack input, @Nonnull CapturedMob mobType);

}
