package crazypants.enderio.item.darksteel.upgrade;

import crazypants.enderio.ModObject;
import crazypants.enderio.config.Config;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionUtils;

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

  public static SpeedUpgrade SPEED_ONE = new SpeedUpgrade("enderio.darksteel.upgrade.speed_one", 1, Config.darkSteelSpeedOneCost);
  public static SpeedUpgrade SPEED_TWO = new SpeedUpgrade("enderio.darksteel.upgrade.speed_two", 2, Config.darkSteelSpeedTwoCost);
  public static SpeedUpgrade SPEED_THREE = new SpeedUpgrade("enderio.darksteel.upgrade.speed_three", 3, Config.darkSteelSpeedThreeCost);

  private short level;
  private float walkMultiplier;
  protected float sprintMultiplier;

  public static boolean isEquipped(EntityPlayer player) {
    ItemStack legs = player.getItemStackFromSlot(EntityEquipmentSlot.LEGS);    
    return loadFromItem(legs) != null;
  }
  
  public static SpeedUpgrade loadFromItem(ItemStack stack) {
    if(stack == null) {
      return null;
    }
    if(stack.getTagCompound() == null) {
      return null;
    }
    if(!stack.getTagCompound().hasKey(KEY_UPGRADE_PREFIX + UPGRADE_NAME)) {
      return null;
    }
    return new SpeedUpgrade((NBTTagCompound) stack.getTagCompound().getTag(KEY_UPGRADE_PREFIX + UPGRADE_NAME));
  }

  private static ItemStack createUpgradeItem() {
    ItemStack pot = new ItemStack(Items.POTIONITEM, 1, 0);
    PotionUtils.addPotionToItemStack(pot, PotionTypes.LONG_SWIFTNESS);        
    return pot;
  }

  public SpeedUpgrade(NBTTagCompound tag) {
    super(UPGRADE_NAME, tag);
    this.level = tag.getShort(KEY_LEVEL);
  }

  public SpeedUpgrade(String unlocName, int level, int levelCost) {
    super(UPGRADE_NAME, unlocName, createUpgradeItem(), levelCost);
    this.level = (short) level;
  }

  @Override
  public boolean canAddToItem(ItemStack stack) {
    if(stack == null || stack.getItem() != ModObject.itemDarkSteelLeggings.getItem() || !EnergyUpgrade.itemHasAnyPowerUpgrade(stack)) {
      return false;
    }
    SpeedUpgrade up = loadFromItem(stack);
    if(up == null) {
      return getLevel() == 1;
    }
    return up.getLevel() == getLevel() - 1;
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
