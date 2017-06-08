package crazypants.enderio.recipe.soul;

import javax.annotation.Nonnull;

import crazypants.enderio.config.Config;
import crazypants.enderio.material.material.Material;
import crazypants.util.CapturedMob;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class SoulBinderReanimationRecipe extends AbstractSoulBinderRecipe {

  public static final @Nonnull SoulBinderReanimationRecipe instance = new SoulBinderReanimationRecipe();

  private SoulBinderReanimationRecipe() {
    super(Config.soulBinderReanimationRF, Config.soulBinderReanimationLevels, "SoulBinderReanimationRecipe", new ResourceLocation("minecraft", "zombie"),
        new ResourceLocation("minecraft", "zombie_villager"));
  }

  @Override
  public @Nonnull ItemStack getInputStack() {
    return Material.ZOMBIE_CONTROLLER.getStack();
  }

  @Override
  public @Nonnull ItemStack getOutputStack() {
    return Material.FRANKEN_ZOMBIE.getStack();
  }

  @Override
  protected @Nonnull ItemStack getOutputStack(@Nonnull ItemStack input, @Nonnull CapturedMob mobType) {
    return getOutputStack();
  }

}
