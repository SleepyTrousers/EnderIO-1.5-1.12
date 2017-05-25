package crazypants.enderio.item.darksteel.upgrade.speed;

import javax.annotation.Nonnull;

import crazypants.enderio.config.Config;
import crazypants.enderio.handler.darksteel.AbstractUpgrade;
import crazypants.enderio.init.ModObject;
import crazypants.enderio.item.darksteel.upgrade.energy.EnergyUpgradeManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionUtils;

public class SpeedUpgrade extends AbstractUpgrade {

  private static final @Nonnull String KEY_LEVEL = "level";

  private static final @Nonnull String KEY_MULTIPLIER = "multiplier";

  private static final @Nonnull String UPGRADE_NAME = "speedBoost";

  public static @Nonnull float[] WALK_MULTIPLIERS = new float[] { Config.darkSteelSpeedOneWalkModifier, Config.darkSteelSpeedTwoWalkMultiplier,
      Config.darkSteelSpeedThreeWalkMultiplier };

  public static @Nonnull float[] SPRINT_MULTIPLIERS = new float[] { Config.darkSteelSpeedOneSprintModifier, Config.darkSteelSpeedTwoSprintMultiplier,
      Config.darkSteelSpeedThreeSprintMultiplier };

  public static final @Nonnull SpeedUpgrade SPEED_ONE = new SpeedUpgrade("enderio.darksteel.upgrade.speed_one", 1, Config.darkSteelSpeedOneCost);
  public static final @Nonnull SpeedUpgrade SPEED_TWO = new SpeedUpgrade("enderio.darksteel.upgrade.speed_two", 2, Config.darkSteelSpeedTwoCost);
  public static final @Nonnull SpeedUpgrade SPEED_THREE = new SpeedUpgrade("enderio.darksteel.upgrade.speed_three", 3, Config.darkSteelSpeedThreeCost);

  private short level;
  private float walkMultiplier;
  protected float sprintMultiplier;

  public static boolean isEquipped(@Nonnull EntityPlayer player) {
    return loadFromItem(player.getItemStackFromSlot(EntityEquipmentSlot.LEGS)) != null;
  }

  public static SpeedUpgrade loadFromItem(@Nonnull ItemStack stack) {
    final NBTTagCompound tagCompound = stack.getTagCompound();
    if (tagCompound == null) {
      return null;
    }
    if (!tagCompound.hasKey(KEY_UPGRADE_PREFIX + UPGRADE_NAME)) {
      return null;
    }
    return new SpeedUpgrade((NBTTagCompound) tagCompound.getTag(KEY_UPGRADE_PREFIX + UPGRADE_NAME));
  }

  private static @Nonnull ItemStack createUpgradeItem() {
    ItemStack pot = new ItemStack(Items.POTIONITEM, 1, 0);
    PotionUtils.addPotionToItemStack(pot, PotionTypes.LONG_SWIFTNESS);
    return pot;
  }

  public SpeedUpgrade(@Nonnull NBTTagCompound tag) {
    super(UPGRADE_NAME, tag);
    this.level = tag.getShort(KEY_LEVEL);
  }

  public SpeedUpgrade(@Nonnull String unlocName, int level, int levelCost) {
    super(UPGRADE_NAME, unlocName, createUpgradeItem(), levelCost);
    this.level = (short) level;
  }

  @Override
  public boolean canAddToItem(@Nonnull ItemStack stack) {
    if (stack.getItem() != ModObject.itemDarkSteelLeggings.getItemNN() || !EnergyUpgradeManager.itemHasAnyPowerUpgrade(stack)) {
      return false;
    }
    SpeedUpgrade up = loadFromItem(stack);
    if (up == null) {
      return getLevel() == 1;
    }
    return up.getLevel() == getLevel() - 1;
  }

  @Override
  public boolean hasUpgrade(@Nonnull ItemStack stack) {
    if (!super.hasUpgrade(stack)) {
      return false;
    }
    SpeedUpgrade up = loadFromItem(stack);
    if (up == null) {
      return false;
    }
    return up.unlocName.equals(unlocName);
  }

  @Override
  public void writeUpgradeToNBT(@Nonnull NBTTagCompound upgradeRoot) {
    upgradeRoot.setShort(KEY_LEVEL, getLevel());
    upgradeRoot.setFloat(KEY_MULTIPLIER, getWalkMultiplier());
  }

  public short getLevel() {
    return level;
  }

  public float getWalkMultiplier() {
    return walkMultiplier;
  }

}
