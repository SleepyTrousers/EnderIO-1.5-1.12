package crazypants.enderio.base.item.darksteel.upgrade.speed;

import java.util.UUID;

import javax.annotation.Nonnull;

import crazypants.enderio.base.config.Config;
import crazypants.enderio.base.handler.darksteel.AbstractUpgrade;
import crazypants.enderio.base.init.ModObject;
import crazypants.enderio.base.item.darksteel.upgrade.energy.EnergyUpgradeManager;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionUtils;

public class SpeedUpgrade extends AbstractUpgrade {

  private static final @Nonnull String UPGRADE_NAME = "speedBoost";

  private static @Nonnull float[] WALK_MULTIPLIERS = new float[] { Config.darkSteelSpeedOneWalkModifier, Config.darkSteelSpeedTwoWalkMultiplier,
      Config.darkSteelSpeedThreeWalkMultiplier };

  private static @Nonnull float[] SPRINT_MULTIPLIERS = new float[] { Config.darkSteelSpeedOneSprintModifier, Config.darkSteelSpeedTwoSprintMultiplier,
      Config.darkSteelSpeedThreeSprintMultiplier };

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
    ItemStack pot = new ItemStack(Items.POTIONITEM, 1, 0);
    PotionUtils.addPotionToItemStack(pot, PotionTypes.LONG_SWIFTNESS);
    return pot;
  }

  public SpeedUpgrade(@Nonnull String unlocName, int level, int levelCost) {
    super(UPGRADE_NAME, level, unlocName, createUpgradeItem(), levelCost);
    this.level = (short) level;
  }

  @Override
  public boolean canAddToItem(@Nonnull ItemStack stack) {
    if (stack.getItem() != ModObject.itemDarkSteelLeggings.getItemNN() || !EnergyUpgradeManager.itemHasAnyPowerUpgrade(stack)) {
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

  public @Nonnull AttributeModifier getModifier(@Nonnull UUID uuid, boolean sprint) {
    return new AttributeModifier(uuid, "generic.movementSpeed", sprint ? SPRINT_MULTIPLIERS[level - 1] : WALK_MULTIPLIERS[level - 1], 1);
  }

}
