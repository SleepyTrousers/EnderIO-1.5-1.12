package crazypants.enderio.item.darksteel.upgrade.jump;

import javax.annotation.Nonnull;

import crazypants.enderio.config.Config;
import crazypants.enderio.handler.darksteel.AbstractUpgrade;
import crazypants.enderio.init.ModObject;
import crazypants.enderio.item.darksteel.upgrade.energy.EnergyUpgradeManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class JumpUpgrade extends AbstractUpgrade {

  private static final @Nonnull String KEY_LEVEL = "level";

  private static final @Nonnull String UPGRADE_NAME = "jumpBoost";

  public static final @Nonnull JumpUpgrade JUMP_ONE = new JumpUpgrade("enderio.darksteel.upgrade.jump_one", 1, Config.darkSteelJumpOneCost);
  public static final @Nonnull JumpUpgrade JUMP_TWO = new JumpUpgrade("enderio.darksteel.upgrade.jump_two", 2, Config.darkSteelJumpTwoCost);
  public static final @Nonnull JumpUpgrade JUMP_THREE = new JumpUpgrade("enderio.darksteel.upgrade.jump_three", 3, Config.darkSteelJumpThreeCost);

  private short level;

  public static boolean isEquipped(@Nonnull EntityPlayer player) {
    return loadFromItem(player.getItemStackFromSlot(EntityEquipmentSlot.FEET)) != null;
  }

  public static JumpUpgrade loadFromItem(@Nonnull ItemStack stack) {
    final NBTTagCompound tagCompound = stack.getTagCompound();
    if (tagCompound == null) {
      return null;
    }
    if (!tagCompound.hasKey(KEY_UPGRADE_PREFIX + UPGRADE_NAME)) {
      return null;
    }
    return new JumpUpgrade((NBTTagCompound) tagCompound.getTag(KEY_UPGRADE_PREFIX + UPGRADE_NAME));
  }

  public JumpUpgrade(@Nonnull NBTTagCompound tag) {
    super(UPGRADE_NAME, tag);
    this.level = tag.getShort(KEY_LEVEL);
  }

  public JumpUpgrade(@Nonnull String unlocName, int level, int levelCost) {
    super(UPGRADE_NAME, unlocName, new ItemStack(Blocks.PISTON), levelCost);
    this.level = (short) level;
  }

  @Override
  public boolean canAddToItem(@Nonnull ItemStack stack) {
    if (stack.getItem() != ModObject.itemDarkSteelBoots.getItemNN() || !EnergyUpgradeManager.itemHasAnyPowerUpgrade(stack)) {
      return false;
    }
    JumpUpgrade up = loadFromItem(stack);
    if (up == null) {
      return getLevel() == 1;
    }
    return up.getLevel() == getLevel() - 1;
  }

  @Override
  public boolean hasUpgrade(@Nonnull ItemStack stack) {
    if (!super.hasUpgrade(stack)) {
      return false;
    }
    JumpUpgrade up = loadFromItem(stack);
    if (up == null) {
      return false;
    }
    return up.unlocName.equals(unlocName);
  }

  @Override
  public void writeUpgradeToNBT(@Nonnull NBTTagCompound upgradeRoot) {
    upgradeRoot.setShort(KEY_LEVEL, getLevel());
  }

  public short getLevel() {
    return level;
  }

}
