package crazypants.enderio.base.item.darksteel.upgrade.flippers;

import javax.annotation.Nonnull;

import crazypants.enderio.base.config.Config;
import crazypants.enderio.base.handler.darksteel.AbstractUpgrade;
import crazypants.enderio.base.init.ModObject;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class SwimUpgrade extends AbstractUpgrade {

  private static final @Nonnull String UPGRADE_NAME = "swim";

  public static final @Nonnull SwimUpgrade INSTANCE = new SwimUpgrade();

  public static SwimUpgrade loadFromItem(@Nonnull ItemStack stack) {
    final NBTTagCompound tagCompound = stack.getTagCompound();
    if (tagCompound == null) {
      return null;
    }
    if (!tagCompound.hasKey(KEY_UPGRADE_PREFIX + UPGRADE_NAME)) {
      return null;
    }
    return new SwimUpgrade((NBTTagCompound) tagCompound.getTag(KEY_UPGRADE_PREFIX + UPGRADE_NAME));
  }

  public SwimUpgrade(@Nonnull NBTTagCompound tag) {
    super(UPGRADE_NAME, tag);
  }

  public SwimUpgrade() {
    super(UPGRADE_NAME, "enderio.darksteel.upgrade.swim", new ItemStack(Blocks.WATERLILY), Config.darkSteelSwimCost);
  }

  @Override
  public boolean canAddToItem(@Nonnull ItemStack stack) {
    if (stack.getItem() != ModObject.itemDarkSteelBoots.getItem()) {
      return false;
    }
    SwimUpgrade up = loadFromItem(stack);
    if (up == null) {
      return true;
    }
    return false;
  }

  @Override
  public void writeUpgradeToNBT(@Nonnull NBTTagCompound upgradeRoot) {
  }

}
