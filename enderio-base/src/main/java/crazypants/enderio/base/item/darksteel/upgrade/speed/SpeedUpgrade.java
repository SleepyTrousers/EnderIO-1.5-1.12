package crazypants.enderio.base.item.darksteel.upgrade.speed;

import javax.annotation.Nonnull;

import crazypants.enderio.api.upgrades.IDarkSteelItem;
import crazypants.enderio.base.config.Config;
import crazypants.enderio.base.handler.darksteel.AbstractUpgrade;
import crazypants.enderio.base.item.darksteel.upgrade.energy.EnergyUpgradeManager;
import crazypants.enderio.base.potion.PotionUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionUtils;

public class SpeedUpgrade extends AbstractUpgrade {

  private static final @Nonnull String UPGRADE_NAME = "speedBoost";

  public static final @Nonnull SpeedUpgrade SPEED_ONE = new SpeedUpgrade("enderio.darksteel.upgrade.speed_one", 1, Config.darkSteelSpeedOneCost);
  public static final @Nonnull SpeedUpgrade SPEED_TWO = new SpeedUpgrade("enderio.darksteel.upgrade.speed_two", 2, Config.darkSteelSpeedTwoCost);
  public static final @Nonnull SpeedUpgrade SPEED_THREE = new SpeedUpgrade("enderio.darksteel.upgrade.speed_three", 3, Config.darkSteelSpeedThreeCost);

  private final short level;

  public static SpeedUpgrade loadAnyFromItem(@Nonnull ItemStack stack) {
    if (SPEED_THREE.hasUpgrade(stack)) {
      return SPEED_THREE;
    }
    if (SPEED_TWO.hasUpgrade(stack)) {
      return SPEED_TWO;
    }
    if (SPEED_ONE.hasUpgrade(stack)) {
      return SPEED_ONE;
    }
    return null;
  }

  public static boolean isEquipped(@Nonnull EntityPlayer player) {
    return loadAnyFromItem(player.getItemStackFromSlot(EntityEquipmentSlot.LEGS)) != null;
  }

  private static @Nonnull ItemStack createUpgradeItem() {
    return PotionUtil.createSwiftnessPotion(true, false);
  }

  public SpeedUpgrade(@Nonnull String unlocName, int level, int levelCost) {
    super(UPGRADE_NAME, level, unlocName, createUpgradeItem(), levelCost);
    this.level = (short) level;
  }

  @Override
  public boolean canAddToItem(@Nonnull ItemStack stack, @Nonnull IDarkSteelItem item) {
    if (!item.isForSlot(EntityEquipmentSlot.LEGS) || !EnergyUpgradeManager.itemHasAnyPowerUpgrade(stack)) {
      return false;
    }
    SpeedUpgrade up = loadAnyFromItem(stack);
    if (up == null) {
      return getLevel() == 1;
    }
    return up.getLevel() == getLevel() - 1;
  }

  public short getLevel() {
    return level;
  }

  @Override
  public boolean isUpgradeItem(@Nonnull ItemStack stack) {
    return super.isUpgradeItem(stack) && PotionUtils.getPotionFromItem(getUpgradeItem()).equals(PotionUtils.getPotionFromItem(stack));
  }

}
