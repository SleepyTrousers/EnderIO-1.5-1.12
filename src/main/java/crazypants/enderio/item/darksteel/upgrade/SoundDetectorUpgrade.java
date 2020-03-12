package crazypants.enderio.item.darksteel.upgrade;

import crazypants.enderio.config.Config;
import crazypants.enderio.item.darksteel.DarkSteelItems;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class SoundDetectorUpgrade extends AbstractUpgrade {

  private static String UPGRADE_NAME = "soundDetector";

  public static final SoundDetectorUpgrade INSTANCE = new SoundDetectorUpgrade();

  public static SoundDetectorUpgrade loadFromItem(ItemStack stack) {
    if(stack == null) {
      return null;
    }
    if(stack.stackTagCompound == null) {
      return null;
    }
    if(!stack.stackTagCompound.hasKey(KEY_UPGRADE_PREFIX + UPGRADE_NAME)) {
      return null;
    }
    return new SoundDetectorUpgrade((NBTTagCompound) stack.stackTagCompound.getTag(KEY_UPGRADE_PREFIX + UPGRADE_NAME));
  }


  public SoundDetectorUpgrade(NBTTagCompound tag) {
    super(UPGRADE_NAME, tag);
  }

  public SoundDetectorUpgrade() {
    super(UPGRADE_NAME, "enderio.darksteel.upgrade.sound", new ItemStack(Blocks.noteblock), Config.darkSteelSoundLocatorCost);
  }

  @Override
  public boolean canAddToItem(ItemStack stack) {
    if(stack == null || !DarkSteelItems.isArmorPart(stack.getItem(), 0)) {
      return false;
    }
    SoundDetectorUpgrade up = loadFromItem(stack);
    if(up == null) {
      return true;
    }
    return false;
  }

  @Override
  public void writeUpgradeToNBT(NBTTagCompound upgradeRoot) {
  }
}