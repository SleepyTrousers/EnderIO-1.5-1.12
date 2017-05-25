package crazypants.enderio.item.darksteel.upgrade.spoon;

import javax.annotation.Nonnull;

import crazypants.enderio.config.Config;
import crazypants.enderio.handler.darksteel.AbstractUpgrade;
import crazypants.enderio.init.ModObject;
import crazypants.enderio.item.darksteel.upgrade.energy.EnergyUpgradeManager;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class SpoonUpgrade extends AbstractUpgrade {

  private static final @Nonnull String UPGRADE_NAME = "spoon";

  public static final @Nonnull SpoonUpgrade INSTANCE = new SpoonUpgrade();

  public static SpoonUpgrade loadFromItem(@Nonnull ItemStack stack) {
    final NBTTagCompound tagCompound = stack.getTagCompound();
    if (tagCompound == null) {
      return null;
    }
    if (!tagCompound.hasKey(KEY_UPGRADE_PREFIX + UPGRADE_NAME)) {
      return null;
    }
    return new SpoonUpgrade((NBTTagCompound) tagCompound.getTag(KEY_UPGRADE_PREFIX + UPGRADE_NAME));
  }

  public SpoonUpgrade(@Nonnull NBTTagCompound tag) {
    super(UPGRADE_NAME, tag);
  }

  public SpoonUpgrade() {
    super(UPGRADE_NAME, "enderio.darksteel.upgrade.spoon", new ItemStack(Items.DIAMOND_SHOVEL), Config.darkSteelSpoonCost);
  }

  @Override
  public boolean canAddToItem(@Nonnull ItemStack stack) {
    if (stack.getItem() != ModObject.itemDarkSteelPickaxe.getItemNN() || !EnergyUpgradeManager.itemHasAnyPowerUpgrade(stack)) {
      return false;
    }
    SpoonUpgrade up = loadFromItem(stack);
    if (up == null) {
      return true;
    }
    return false;
  }

  @Override
  public void writeUpgradeToNBT(@Nonnull NBTTagCompound upgradeRoot) {
  }

}
