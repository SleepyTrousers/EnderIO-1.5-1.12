package crazypants.enderio.base.item.darksteel.upgrade.nightvision;

import javax.annotation.Nonnull;

import crazypants.enderio.api.upgrades.IDarkSteelItem;
import crazypants.enderio.base.config.config.DarkSteelConfig;
import crazypants.enderio.base.handler.darksteel.AbstractUpgrade;
import crazypants.enderio.base.potion.PotionUtil;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionUtils;

public class NightVisionUpgrade extends AbstractUpgrade {

  private static final @Nonnull String UPGRADE_NAME = "nightVision";

  public static final @Nonnull NightVisionUpgrade INSTANCE = new NightVisionUpgrade();

  private static @Nonnull ItemStack createUpgradeItem() {
    return PotionUtil.createNightVisionPotion(false, false);
  }

  public NightVisionUpgrade() {
    super(UPGRADE_NAME, "enderio.darksteel.upgrade.nightVision", createUpgradeItem(), DarkSteelConfig.nightVisionCost);
  }

  @Override
  public boolean canAddToItem(@Nonnull ItemStack stack, @Nonnull IDarkSteelItem item) {
    return item.isForSlot(EntityEquipmentSlot.HEAD) && !hasUpgrade(stack, item);
  }

  @Override
  public boolean isUpgradeItem(@Nonnull ItemStack stack) {
    return super.isUpgradeItem(stack) && PotionUtils.getPotionFromItem(getUpgradeItem()).equals(PotionUtils.getPotionFromItem(stack));
  }

}
