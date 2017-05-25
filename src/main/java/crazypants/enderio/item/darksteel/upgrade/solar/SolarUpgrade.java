package crazypants.enderio.item.darksteel.upgrade.solar;

import javax.annotation.Nonnull;

import crazypants.enderio.handler.darksteel.AbstractUpgrade;
import crazypants.enderio.handler.darksteel.IRenderUpgrade;
import crazypants.enderio.init.ModObject;
import crazypants.enderio.item.darksteel.upgrade.energy.EnergyUpgradeManager;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class SolarUpgrade extends AbstractUpgrade {

  private static final @Nonnull String KEY_LEVEL = "level";
  
  private static final @Nonnull String UPGRADE_NAME = "solar";
  
  public static SolarUpgrade loadFromItem(@Nonnull ItemStack stack) {
    final NBTTagCompound tagCompound = stack.getTagCompound();
    if (tagCompound == null) {
      return null;
    }
    if (!tagCompound.hasKey(KEY_UPGRADE_PREFIX + UPGRADE_NAME)) {
      return null;
    }
    return new SolarUpgrade((NBTTagCompound) tagCompound.getTag(KEY_UPGRADE_PREFIX + UPGRADE_NAME));
  }

  private final int level;

  public SolarUpgrade(@Nonnull NBTTagCompound tag) {
    super(UPGRADE_NAME, tag);
    level = tag.getByte(KEY_LEVEL);
  }

  public SolarUpgrade(@Nonnull String unlocName, @Nonnull ItemStack item, int level, int levelCost) {
    super(UPGRADE_NAME, unlocName, item, levelCost);
    this.level = level;
  }
  
  @Override
  public boolean canAddToItem(@Nonnull ItemStack stack) {
    if (stack.getItem() != ModObject.itemDarkSteelHelmet.getItemNN() || !EnergyUpgradeManager.itemHasAnyPowerUpgrade(stack)) {
        return false;
      }
      SolarUpgrade up = loadFromItem(stack);
      if(up == null) {
        return getLevel() == 1;
      }
      return up.getLevel() == getLevel() - 1;
  }
  
  @Override
  public boolean hasUpgrade(@Nonnull ItemStack stack) {
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
  public void writeUpgradeToNBT(@Nonnull NBTTagCompound upgradeRoot) {
    upgradeRoot.setByte(KEY_LEVEL, (byte) getLevel());
  }

  public int getRFPerSec() {
    return SolarUpgradeManager.getRFforLevel(level);
  }
  
  @Override
  @SideOnly(Side.CLIENT)
  public IRenderUpgrade getRender() {
    return SolarUpgradeLayer.instance;
  }

  public int getLevel() {
    return level;
  }

}
