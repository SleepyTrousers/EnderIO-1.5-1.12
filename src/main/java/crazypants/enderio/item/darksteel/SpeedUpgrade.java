package crazypants.enderio.item.darksteel;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import crazypants.enderio.Config;
import crazypants.enderio.EnderIO;
import crazypants.enderio.material.Material;

public class SpeedUpgrade extends AbstractUpgrade {

  private static final String KEY_LEVEL = "level";

  private static final String KEY_MULTIPLIER = "multiplier";

  private static String UPGRADE_NAME = "speedBoost";

  public static float[] WALK_MULTIPLIERS = new float[] {
    Config.darkSteelSpeedOneWalkModifier,
    Config.darkSteelSpeedTwoWalkMultiplier,
    Config.darkSteelSpeedThreeWalkMultiplier
  };

  public static float[] SPRINT_MULTIPLIERS = new float[] {
    Config.darkSteelSpeedOneSprintModifier,
    Config.darkSteelSpeedTwoSprintMultiplier,
    Config.darkSteelSpeedThreeSprintMultiplier
  };

  public static SpeedUpgrade SPEED_ONE = new SpeedUpgrade("enderio.darksteel.upgrade.speed_one", 1, 15);
  public static SpeedUpgrade SPEED_TWO = new SpeedUpgrade("enderio.darksteel.upgrade.speed_two", 2, 20);
  public static SpeedUpgrade SPEED_THREE = new SpeedUpgrade("enderio.darksteel.upgrade.speed_three", 3, 30);

  protected short level;
  protected float walkMultiplier;
  protected float sprintMultiplier;

  public static SpeedUpgrade loadFromItem(ItemStack stack) {
    if(stack == null) {
      return null;
    }
    if(stack.stackTagCompound == null) {
      return null;
    }
    if(!stack.stackTagCompound.hasKey(KEY_UPGRADE_PREFIX + UPGRADE_NAME)) {
      return null;
    }
    return new SpeedUpgrade((NBTTagCompound) stack.stackTagCompound.getTag(KEY_UPGRADE_PREFIX + UPGRADE_NAME));
  }

  public SpeedUpgrade(NBTTagCompound tag) {
    super(UPGRADE_NAME, tag);
    level = tag.getShort(KEY_LEVEL);
  }

  public SpeedUpgrade(String unlocName,int level, int levelCost) {
    super(UPGRADE_NAME, unlocName, new ItemStack(EnderIO.itemMaterial, 1, Material.PULSATING_CYSTAL.ordinal()), levelCost);
    this.level = (short)level;
  }

  @Override
  public boolean canAddToItem(ItemStack stack) {
    if(stack == null || stack.getItem() != EnderIO.itemDarkSteelLeggings || !EnergyUpgrade.itemHasAnyPowerUpgrade(stack)) {
      return false;
    }
    SpeedUpgrade up = loadFromItem(stack);
    if(up == null) {
      return level == 1;
    }
    return up.level == level - 1;
  }


  @Override
  public boolean hasUpgrade(ItemStack stack) {
    if(!super.hasUpgrade(stack)) {
      return false;
    }
    SpeedUpgrade up = loadFromItem(stack);
    if(up == null) {
      return false;
    }
    return up.unlocName.equals(unlocName);
  }

  @Override
  public void writeUpgradeToNBT(NBTTagCompound upgradeRoot) {
    upgradeRoot.setShort(KEY_LEVEL, level);
    upgradeRoot.setFloat(KEY_MULTIPLIER, walkMultiplier);
  }

}
