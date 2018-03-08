package crazypants.enderio.base.item.darksteel.upgrade.sound;

import javax.annotation.Nonnull;

import crazypants.enderio.api.upgrades.IDarkSteelItem;
import crazypants.enderio.base.config.Config;
import crazypants.enderio.base.handler.darksteel.AbstractUpgrade;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;

public class SoundDetectorUpgrade extends AbstractUpgrade {

  private static final @Nonnull String UPGRADE_NAME = "soundDetector";

  public static final @Nonnull SoundDetectorUpgrade INSTANCE = new SoundDetectorUpgrade();

  public SoundDetectorUpgrade() {
    super(UPGRADE_NAME, "enderio.darksteel.upgrade.sound", new ItemStack(Blocks.NOTEBLOCK), Config.darkSteelSoundLocatorCost);
  }

  @Override
  public boolean canAddToItem(@Nonnull ItemStack stack, @Nonnull IDarkSteelItem item) {
    return item.isForSlot(EntityEquipmentSlot.HEAD) && !hasUpgrade(stack, item);
  }

}