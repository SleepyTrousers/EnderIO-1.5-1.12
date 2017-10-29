package crazypants.enderio.machine.soul;

import crazypants.enderio.config.Config;
import crazypants.enderio.init.ModObject;
import crazypants.enderio.material.material.Material;
import crazypants.util.CapturedMob;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class SoulBinderPrecientCystalRecipe extends AbstractSoulBinderRecipe {

  public static SoulBinderPrecientCystalRecipe instance = new SoulBinderPrecientCystalRecipe();

  private SoulBinderPrecientCystalRecipe() {
    super(Config.soulBinderPrecientCystalRF, Config.soulBinderPrecientCystalLevels, "SoulBinderPrecientCystalRecipe", new ResourceLocation("minecraft:shulker"));
  }

  @Override
  public ItemStack getInputStack() {
    return new ItemStack(ModObject.itemMaterial.getItem(), 1, Material.VIBRANT_CYSTAL.ordinal());
  }

  @Override
  public ItemStack getOutputStack() {
    return new ItemStack(ModObject.itemMaterial.getItem(), 1, Material.PRECIENT_CRYSTAL.ordinal());
  }

  @Override
  protected ItemStack getOutputStack(ItemStack input, CapturedMob mobType) {
    return getOutputStack();
  }

}
