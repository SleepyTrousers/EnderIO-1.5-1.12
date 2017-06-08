package crazypants.enderio.recipe.soul;

import javax.annotation.Nonnull;

import crazypants.enderio.config.Config;
import crazypants.enderio.material.material.Material;
import crazypants.util.CapturedMob;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class SoulBinderSentientRecipe extends AbstractSoulBinderRecipe {

  public static final @Nonnull SoulBinderSentientRecipe instance = new SoulBinderSentientRecipe();

  private SoulBinderSentientRecipe() {
    super(Config.soulBinderReanimationRF, Config.soulBinderReanimationLevels, "SoulBinderSentientRecipe", new ResourceLocation("minecraft", "witch"),
        new ResourceLocation("enderzoo", "witherwitch"));
  }

  @Override
  public @Nonnull ItemStack getInputStack() {
    return Material.ENDER_RESONATOR.getStack();
  }

  @Override
  public @Nonnull ItemStack getOutputStack() {
    return Material.SENTIENT_ENDER.getStack();
  }

  @Override
  protected @Nonnull ItemStack getOutputStack(@Nonnull ItemStack input, @Nonnull CapturedMob mobType) {
    return getOutputStack();
  }

}
