package crazypants.enderio.item.darksteel.upgrade.sound;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import crazypants.enderio.config.Config;
import crazypants.enderio.handler.darksteel.AbstractUpgrade;
import crazypants.enderio.item.darksteel.DarkSteelItems;

public class SoundDetectorUpgrade extends AbstractUpgrade {

  private static String UPGRADE_NAME = "soundDetector";

  public static final SoundDetectorUpgrade INSTANCE = new SoundDetectorUpgrade();

  public static SoundDetectorUpgrade loadFromItem(ItemStack stack) {
    if (stack == null) {
      return null;
    }
    if (!stack.hasTagCompound()) {
      return null;
    }
    if (!stack.getTagCompound().hasKey(KEY_UPGRADE_PREFIX + UPGRADE_NAME)) {
      return null;
    }
    return new SoundDetectorUpgrade((NBTTagCompound) stack.getTagCompound().getTag(KEY_UPGRADE_PREFIX + UPGRADE_NAME));
  }

  public SoundDetectorUpgrade(NBTTagCompound tag) {
    super(UPGRADE_NAME, tag);
  }

  public SoundDetectorUpgrade() {
    super(UPGRADE_NAME, "enderio.darksteel.upgrade.sound", new ItemStack(Blocks.NOTEBLOCK), Config.darkSteelSoundLocatorCost);
  }

  @Override
  public boolean canAddToItem(ItemStack stack) {
    if (stack == null || stack.getItem() != ModObject.itemDarkSteelHelmet) {
      return false;
    }
    SoundDetectorUpgrade up = loadFromItem(stack);
    if (up == null) {
      return true;
    }
    return false;
  }

  @Override
  public void writeUpgradeToNBT(NBTTagCompound upgradeRoot) {
  }
}