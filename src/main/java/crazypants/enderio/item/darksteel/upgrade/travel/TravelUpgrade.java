package crazypants.enderio.item.darksteel.upgrade.travel;

import javax.annotation.Nonnull;

import crazypants.enderio.config.Config;
import crazypants.enderio.handler.darksteel.AbstractUpgrade;
import crazypants.enderio.init.ModObject;
import crazypants.enderio.item.darksteel.upgrade.energy.EnergyUpgradeManager;
import crazypants.enderio.material.material.Material;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class TravelUpgrade extends AbstractUpgrade {

  private static final @Nonnull String UPGRADE_NAME = "travel";

  public static final @Nonnull TravelUpgrade INSTANCE = new TravelUpgrade();

  public static TravelUpgrade loadFromItem(@Nonnull ItemStack stack) {
    final NBTTagCompound tagCompound = stack.getTagCompound();
    if (tagCompound == null) {
      return null;
    }
    if (!tagCompound.hasKey(KEY_UPGRADE_PREFIX + UPGRADE_NAME)) {
      return null;
    }
    return new TravelUpgrade((NBTTagCompound) tagCompound.getTag(KEY_UPGRADE_PREFIX + UPGRADE_NAME));
  }

  public TravelUpgrade(@Nonnull NBTTagCompound tag) {
    super(UPGRADE_NAME, tag);
  }

  public TravelUpgrade() {
    super(UPGRADE_NAME, "enderio.darksteel.upgrade.travel", Material.ENDER_CRYSTAL.getStack(), Config.darkSteelTravelCost);
  }

  @Override
  public boolean canAddToItem(@Nonnull ItemStack stack) {
    if ((stack.getItem() != ModObject.itemDarkSteelSword.getItemNN() && stack.getItem() != ModObject.itemDarkSteelPickaxe.getItemNN())
        || !EnergyUpgradeManager.itemHasAnyPowerUpgrade(stack)) {
      return false;
    }
    TravelUpgrade up = loadFromItem(stack);
    if (up == null) {
      return true;
    }
    return false;
  }

  @Override
  public void writeUpgradeToNBT(@Nonnull NBTTagCompound upgradeRoot) {
  }

}
