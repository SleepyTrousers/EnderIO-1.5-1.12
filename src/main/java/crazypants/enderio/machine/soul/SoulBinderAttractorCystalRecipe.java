package crazypants.enderio.machine.soul;

import crazypants.enderio.config.Config;
import crazypants.enderio.material.Material;
import crazypants.util.CapturedMob;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import static crazypants.enderio.ModObject.itemMaterial;

public class SoulBinderAttractorCystalRecipe extends AbstractSoulBinderRecipe {

  public static SoulBinderAttractorCystalRecipe instance = new SoulBinderAttractorCystalRecipe();

  private SoulBinderAttractorCystalRecipe() {
    super(Config.soulBinderAttractorCystalRF, Config.soulBinderAttractorCystalLevels, "SoulBinderAttractorCystalRecipe", EntityVillager.class);
  }

  @Override
  public ItemStack getInputStack() {    
    return new ItemStack(Items.EMERALD);
  }

  @Override
  public ItemStack getOutputStack() {
    return new ItemStack(itemMaterial.getItem(), 1, Material.ATTRACTOR_CRYSTAL.ordinal());
  }

  @Override
  protected ItemStack getOutputStack(ItemStack input, CapturedMob mobType) {
    return getOutputStack();
  }

}
