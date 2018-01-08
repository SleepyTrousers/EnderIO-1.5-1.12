package crazypants.enderio.base.item.darksteel.upgrade.sound;

import javax.annotation.Nonnull;

import crazypants.enderio.base.config.Config;
import crazypants.enderio.base.handler.darksteel.AbstractUpgrade;
import crazypants.enderio.base.init.ModObject;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

public class SoundDetectorUpgrade extends AbstractUpgrade {

  private static final @Nonnull String UPGRADE_NAME = "soundDetector";

  public static final @Nonnull SoundDetectorUpgrade INSTANCE = new SoundDetectorUpgrade();

  public SoundDetectorUpgrade() {
    super(UPGRADE_NAME, "enderio.darksteel.upgrade.sound", new ItemStack(Blocks.NOTEBLOCK), Config.darkSteelSoundLocatorCost);
  }

  @Override
  public boolean canAddToItem(@Nonnull ItemStack stack) {
    return stack.getItem() == ModObject.itemDarkSteelHelmet.getItemNN() && !hasUpgrade(stack);
  }

}