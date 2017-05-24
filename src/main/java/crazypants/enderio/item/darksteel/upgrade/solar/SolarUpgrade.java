package crazypants.enderio.item.darksteel.upgrade.solar;

import crazypants.enderio.config.Config;
import crazypants.enderio.handler.darksteel.AbstractUpgrade;
import crazypants.enderio.handler.darksteel.IRenderUpgrade;
import crazypants.enderio.item.darksteel.DarkSteelItems;
import crazypants.enderio.item.darksteel.upgrade.energy.EnergyUpgrade;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static crazypants.enderio.ModObject.blockSolarPanel;

public class SolarUpgrade extends AbstractUpgrade {

  private static final String KEY_LEVEL = "level";
  
  private static final String UPGRADE_NAME = "solar";
  
  public static final SolarUpgrade SOLAR_ONE = new SolarUpgrade("enderio.darksteel.upgrade.solar_one", (byte) 1, Config.darkSteelSolarOneCost);
  public static final SolarUpgrade SOLAR_TWO = new SolarUpgrade("enderio.darksteel.upgrade.solar_two", (byte) 2, Config.darkSteelSolarTwoCost);
  public static final SolarUpgrade SOLAR_THREE = new SolarUpgrade("enderio.darksteel.upgrade.solar_three", (byte) 3, Config.darkSteelSolarThreeCost);
  
  public static SolarUpgrade loadFromItem(ItemStack stack) {
    if(stack == null) {
      return null;
    }
    if(stack.getTagCompound() == null) {
      return null;
    }
    if(!stack.getTagCompound().hasKey(KEY_UPGRADE_PREFIX + UPGRADE_NAME)) {
      return null;
    }
    return new SolarUpgrade((NBTTagCompound) stack.getTagCompound().getTag(KEY_UPGRADE_PREFIX + UPGRADE_NAME));
  }

  private final byte level;

  public SolarUpgrade(NBTTagCompound tag) {
    super(UPGRADE_NAME, tag);
    level = tag.getByte(KEY_LEVEL);
  }

  public SolarUpgrade(String unlocName, byte level, int levelCost) {
    super(UPGRADE_NAME, unlocName, new ItemStack(blockSolarPanel.getBlock(), 1, level - 1), levelCost);
    this.level = level;
  }
  
  @Override
  public boolean canAddToItem(ItemStack stack) {
      if(stack == null || stack.getItem() != ModObject.itemDarkSteelHelmet || !EnergyUpgrade.itemHasAnyPowerUpgrade(stack)) {
        return false;
      }
      SolarUpgrade up = loadFromItem(stack);
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
    SolarUpgrade up = loadFromItem(stack);
    if(up == null) {
      return false;
    }
    return up.unlocName.equals(unlocName);
  }
  
  @Override
  public void writeUpgradeToNBT(NBTTagCompound upgradeRoot) {
    upgradeRoot.setByte(KEY_LEVEL, getLevel());
  }

  public int getRFPerSec() {
    return getLevel() == 1 ? Config.darkSteelSolarOneGen : getLevel() == 2 ? Config.darkSteelSolarTwoGen : Config.darkSteelSolarThreeGen;
  }
  
  @Override
  @SideOnly(Side.CLIENT)
  public IRenderUpgrade getRender() {
    return SolarUpgradeLayer.instance;
  }

  public byte getLevel() {
    return level;
  }

}
