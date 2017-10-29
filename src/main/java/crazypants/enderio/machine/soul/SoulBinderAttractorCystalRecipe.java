package crazypants.enderio.machine.soul;

import static crazypants.enderio.machine.MachineObject.itemMaterial;

import crazypants.enderio.config.Config;
import crazypants.enderio.material.material.Material;
import crazypants.util.CapturedMob;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

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
