package crazypants.enderio.base.recipe.soul;

import javax.annotation.Nonnull;

import crazypants.enderio.base.config.Config;
import crazypants.enderio.base.material.material.Material;
import crazypants.enderio.util.CapturedMob;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class SoulBinderAttractorCystalRecipe extends AbstractSoulBinderRecipe {

  public static final @Nonnull SoulBinderAttractorCystalRecipe instance = new SoulBinderAttractorCystalRecipe();

  private SoulBinderAttractorCystalRecipe() {
    super(Config.soulBinderAttractorCystalRF, Config.soulBinderAttractorCystalLevels, "SoulBinderAttractorCystalRecipe", EntityVillager.class);
  }

  @Override
  public @Nonnull ItemStack getInputStack() {
    return new ItemStack(Items.EMERALD);
  }

  @Override
  public @Nonnull ItemStack getOutputStack() {
    return Material.ATTRACTOR_CRYSTAL.getStack();
  }

  @Override
  protected @Nonnull ItemStack getOutputStack(@Nonnull ItemStack input, @Nonnull CapturedMob mobType) {
    return getOutputStack();
  }

}
