package crazypants.enderio.recipe.soul;

import javax.annotation.Nonnull;

import crazypants.enderio.config.Config;
import crazypants.enderio.material.material.Material;
import crazypants.util.CapturedMob;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class SoulBinderPrecientCystalRecipe extends AbstractSoulBinderRecipe {

  public static final @Nonnull SoulBinderPrecientCystalRecipe instance = new SoulBinderPrecientCystalRecipe();

  private SoulBinderPrecientCystalRecipe() {
    super(Config.soulBinderPrecientCystalRF, Config.soulBinderPrecientCystalLevels, "SoulBinderPrecientCystalRecipe",
        new ResourceLocation("minecraft", "shulker"));
  }

  @Override
  public @Nonnull ItemStack getInputStack() {
    return Material.VIBRANT_CYSTAL.getStack();
  }

  @Override
  public @Nonnull ItemStack getOutputStack() {
    return Material.PRECIENT_CRYSTAL.getStack();
  }

  @Override
  protected @Nonnull ItemStack getOutputStack(@Nonnull ItemStack input, @Nonnull CapturedMob mobType) {
    return getOutputStack();
  }

}
