package crazypants.enderio.base.integration.thaumcraft;

import javax.annotation.Nonnull;

import crazypants.enderio.base.config.Config;
import crazypants.enderio.base.handler.darksteel.AbstractUpgrade;
import crazypants.enderio.base.init.ModObject;
import crazypants.enderio.util.Prep;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public class GogglesOfRevealingUpgrade extends AbstractUpgrade {

  private static final @Nonnull String UPGRADE_NAME = "gogglesRevealing";

  public static final @Nonnull GogglesOfRevealingUpgrade INSTANCE = new GogglesOfRevealingUpgrade();

  public static @Nonnull ItemStack getGoggles() {
    Item i = Item.REGISTRY.getObject(new ResourceLocation("Thaumcraft", "ItemGoggles"));
    if (i != null) {
      return new ItemStack(i);
    }
    return Prep.getEmpty();
  }

  public static GogglesOfRevealingUpgrade loadFromItem(@Nonnull ItemStack stack) {
    final NBTTagCompound tagCompound = stack.getTagCompound();
    if (tagCompound == null) {
      return null;
    }
    if (!tagCompound.hasKey(KEY_UPGRADE_PREFIX + UPGRADE_NAME)) {
      return null;
    }
    return new GogglesOfRevealingUpgrade((NBTTagCompound) tagCompound.getTag(KEY_UPGRADE_PREFIX + UPGRADE_NAME));
  }

  public static boolean isUpgradeEquipped(@Nonnull EntityPlayer player) {
    ItemStack helmet = player.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
    return GogglesOfRevealingUpgrade.loadFromItem(helmet) != null;
  }

  public GogglesOfRevealingUpgrade(@Nonnull NBTTagCompound tag) {
    super(UPGRADE_NAME, tag);
  }

  public GogglesOfRevealingUpgrade() {
    super(UPGRADE_NAME, "enderio.darksteel.upgrade.gogglesOfRevealing", getGoggles(), Config.darkSteelGogglesOfRevealingCost);
  }

  @Override
  public boolean canAddToItem(@Nonnull ItemStack stack) {
    if (stack.getItem() != ModObject.itemDarkSteelHelmet.getItemNN() || Prep.isInvalid(getGoggles())) {
      return false;
    }
    GogglesOfRevealingUpgrade up = loadFromItem(stack);
    if (up == null) {
      return true;
    }
    return false;
  }

  @Override
  public void writeUpgradeToNBT(@Nonnull NBTTagCompound upgradeRoot) {
  }

  @Override
  public @Nonnull ItemStack getUpgradeItem() {
    if (Prep.isValid(upgradeItem)) {
      return upgradeItem;
    }
    upgradeItem = getGoggles();
    return upgradeItem;
  }

  @Override
  public @Nonnull String getUpgradeItemName() {
    if (Prep.isInvalid(getUpgradeItem())) {
      return "Goggles of Revealing";
    }
    return super.getUpgradeItemName();
  }

}
