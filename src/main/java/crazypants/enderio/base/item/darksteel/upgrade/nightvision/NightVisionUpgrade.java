package crazypants.enderio.base.item.darksteel.upgrade.nightvision;

import javax.annotation.Nonnull;

import crazypants.enderio.base.config.Config;
import crazypants.enderio.base.handler.darksteel.AbstractUpgrade;
import crazypants.enderio.base.init.ModObject;
import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionUtils;

public class NightVisionUpgrade extends AbstractUpgrade {

  private static final @Nonnull String UPGRADE_NAME = "nightVision";

  public static final @Nonnull NightVisionUpgrade INSTANCE = new NightVisionUpgrade();

  public static NightVisionUpgrade loadFromItem(@Nonnull ItemStack stack) {
    final NBTTagCompound tagCompound = stack.getTagCompound();
    if (tagCompound == null) {
      return null;
    }
    if (!tagCompound.hasKey(KEY_UPGRADE_PREFIX + UPGRADE_NAME)) {
      return null;
    }
    return new NightVisionUpgrade((NBTTagCompound) tagCompound.getTag(KEY_UPGRADE_PREFIX + UPGRADE_NAME));
  }

  private static @Nonnull ItemStack createUpgradeItem() {
    ItemStack pot = new ItemStack(Items.POTIONITEM, 1, 0);
    PotionUtils.addPotionToItemStack(pot, PotionTypes.NIGHT_VISION);
    return pot;
  }

  public NightVisionUpgrade(@Nonnull NBTTagCompound tag) {
    super(UPGRADE_NAME, tag);
  }

  public NightVisionUpgrade() {
    super(UPGRADE_NAME, "enderio.darksteel.upgrade.nightVision", createUpgradeItem(), Config.darkSteelNightVisionCost);
  }

  @Override
  public boolean canAddToItem(@Nonnull ItemStack stack) {
    if (stack.getItem() != ModObject.itemDarkSteelHelmet.getItemNN()) {
      return false;
    }
    NightVisionUpgrade up = loadFromItem(stack);
    if (up == null) {
      return true;
    }
    return false;
  }

  @Override
  public void writeUpgradeToNBT(@Nonnull NBTTagCompound upgradeRoot) {
  }

}
