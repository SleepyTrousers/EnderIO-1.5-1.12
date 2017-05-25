package crazypants.enderio.item.darksteel.upgrade.sound;

import javax.annotation.Nonnull;

import crazypants.enderio.config.Config;
import crazypants.enderio.handler.darksteel.AbstractUpgrade;
import crazypants.enderio.init.ModObject;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class SoundDetectorUpgrade extends AbstractUpgrade {

  private static final @Nonnull String UPGRADE_NAME = "soundDetector";

  public static final @Nonnull SoundDetectorUpgrade INSTANCE = new SoundDetectorUpgrade();

  public static SoundDetectorUpgrade loadFromItem(@Nonnull ItemStack stack) {
    final NBTTagCompound tagCompound = stack.getTagCompound();
    if (tagCompound == null) {
      return null;
    }
    if (!tagCompound.hasKey(KEY_UPGRADE_PREFIX + UPGRADE_NAME)) {
      return null;
    }
    return new SoundDetectorUpgrade((NBTTagCompound) tagCompound.getTag(KEY_UPGRADE_PREFIX + UPGRADE_NAME));
  }

  public SoundDetectorUpgrade(@Nonnull NBTTagCompound tag) {
    super(UPGRADE_NAME, tag);
  }

  public SoundDetectorUpgrade() {
    super(UPGRADE_NAME, "enderio.darksteel.upgrade.sound", new ItemStack(Blocks.NOTEBLOCK), Config.darkSteelSoundLocatorCost);
  }

  @Override
  public boolean canAddToItem(@Nonnull ItemStack stack) {
    if (stack.getItem() != ModObject.itemDarkSteelHelmet.getItemNN()) {
      return false;
    }
    SoundDetectorUpgrade up = loadFromItem(stack);
    if (up == null) {
      return true;
    }
    return false;
  }

  @Override
  public void writeUpgradeToNBT(@Nonnull NBTTagCompound upgradeRoot) {
  }

}