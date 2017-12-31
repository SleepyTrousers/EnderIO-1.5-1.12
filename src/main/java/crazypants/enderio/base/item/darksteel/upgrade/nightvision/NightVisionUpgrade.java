package crazypants.enderio.base.item.darksteel.upgrade.nightvision;

import javax.annotation.Nonnull;

import crazypants.enderio.base.config.Config;
import crazypants.enderio.base.handler.darksteel.AbstractUpgrade;
import crazypants.enderio.base.init.ModObject;
import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionUtils;

public class NightVisionUpgrade extends AbstractUpgrade {

  private static final @Nonnull String UPGRADE_NAME = "nightVision";

  public static final @Nonnull NightVisionUpgrade INSTANCE = new NightVisionUpgrade();

  private static @Nonnull ItemStack createUpgradeItem() {
    ItemStack pot = new ItemStack(Items.POTIONITEM, 1, 0);
    PotionUtils.addPotionToItemStack(pot, PotionTypes.NIGHT_VISION);
    return pot;
  }

  public NightVisionUpgrade() {
    super(UPGRADE_NAME, "enderio.darksteel.upgrade.nightVision", createUpgradeItem(), Config.darkSteelNightVisionCost);
  }

  @Override
  public boolean canAddToItem(@Nonnull ItemStack stack) {
    return stack.getItem() == ModObject.itemDarkSteelHelmet.getItemNN() && !hasUpgrade(stack);
  }

}
