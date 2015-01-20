package crazypants.enderio.item.darksteel;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionHelper;
import crazypants.enderio.EnderIO;
import crazypants.enderio.config.Config;

public class SolarUpgrade extends AbstractUpgrade {

  private static final String KEY_LEVEL = "level";
  
  private static final String UPGRADE_NAME = "speedBoost";
  
  public static final SolarUpgrade SOLAR_ONE = new SolarUpgrade("enderio.darksteel.upgrade.solar_one", (byte) 1, Config.darkSteelSolarOneCost);
  public static final SolarUpgrade SOLAR_TWO = new SolarUpgrade("enderio.darksteel.upgrade.solar_two", (byte) 2, Config.darkSteelSolarTwoCost);

  public static SolarUpgrade loadFromItem(ItemStack stack) {
    if(stack == null) {
      return null;
    }
    if(stack.stackTagCompound == null) {
      return null;
    }
    if(!stack.stackTagCompound.hasKey(KEY_UPGRADE_PREFIX + UPGRADE_NAME)) {
      return null;
    }
    return new SolarUpgrade((NBTTagCompound) stack.stackTagCompound.getTag(KEY_UPGRADE_PREFIX + UPGRADE_NAME));
  }

  private static ItemStack createUpgradeItem() {
    ItemStack pot = new ItemStack(Items.potionitem, 1, 0);
    int res = PotionHelper.applyIngredient(0, Items.nether_wart.getPotionEffect(new ItemStack(Items.nether_wart)));
    res = PotionHelper.applyIngredient(res, PotionHelper.sugarEffect);    
    pot.setItemDamage(res);
    return pot;
  }
  
  private byte level;

  public SolarUpgrade(NBTTagCompound tag) {
    super(UPGRADE_NAME, tag);
    level = tag.getByte(KEY_LEVEL);
  }

  public SolarUpgrade(String unlocName, byte level, int levelCost) {
    super(UPGRADE_NAME, unlocName, createUpgradeItem(), levelCost);
    this.level = (byte) level;
  }
  
  @Override
  public boolean canAddToItem(ItemStack stack) {
      if(stack == null || stack.getItem() != EnderIO.itemDarkSteelHelmet || !EnergyUpgrade.itemHasAnyPowerUpgrade(stack)) {
        return false;
      }
      SolarUpgrade up = loadFromItem(stack);
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
    SolarUpgrade up = loadFromItem(stack);
    if(up == null) {
      return false;
    }
    return up.unlocName.equals(unlocName);
  }
  
  @Override
  public ItemStack getUpgradeItem() {
    return new ItemStack(EnderIO.blockSolarPanel, 1, level - 1);
  }

  @Override
  public void writeUpgradeToNBT(NBTTagCompound upgradeRoot) {
    upgradeRoot.setByte(KEY_LEVEL, level);
  }

  public int getRFPerSec() {
    return level == 1 ? Config.darkSteelSolarOneGen : Config.darkSteelSolarTwoGen;
  }
}
