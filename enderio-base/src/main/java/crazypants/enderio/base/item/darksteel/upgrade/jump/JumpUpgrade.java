package crazypants.enderio.base.item.darksteel.upgrade.jump;

import javax.annotation.Nonnull;

import crazypants.enderio.base.config.Config;
import crazypants.enderio.base.handler.darksteel.AbstractUpgrade;
import crazypants.enderio.base.init.ModObject;
import crazypants.enderio.base.item.darksteel.upgrade.energy.EnergyUpgradeManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;

public class JumpUpgrade extends AbstractUpgrade {

  private static final @Nonnull String UPGRADE_NAME = "jumpBoost";

  public static final @Nonnull JumpUpgrade JUMP_ONE = new JumpUpgrade("enderio.darksteel.upgrade.jump_one", 1, Config.darkSteelJumpOneCost);
  public static final @Nonnull JumpUpgrade JUMP_TWO = new JumpUpgrade("enderio.darksteel.upgrade.jump_two", 2, Config.darkSteelJumpTwoCost);
  public static final @Nonnull JumpUpgrade JUMP_THREE = new JumpUpgrade("enderio.darksteel.upgrade.jump_three", 3, Config.darkSteelJumpThreeCost);

  private final short level;

  public static JumpUpgrade loadAnyFromItem(@Nonnull ItemStack stack) {
    if (JUMP_THREE.hasUpgrade(stack)) {
      return JUMP_THREE;
    }
    if (JUMP_TWO.hasUpgrade(stack)) {
      return JUMP_TWO;
    }
    if (JUMP_ONE.hasUpgrade(stack)) {
      return JUMP_ONE;
    }
    return null;
  }

  public static boolean isEquipped(@Nonnull EntityPlayer player) {
    return loadAnyFromItem(player.getItemStackFromSlot(EntityEquipmentSlot.FEET)) != null;
  }

  public JumpUpgrade(@Nonnull String unlocName, int level, int levelCost) {
    super(UPGRADE_NAME, level, unlocName, new ItemStack(Blocks.PISTON), levelCost);
    this.level = (short) level;
  }

  @Override
  public boolean canAddToItem(@Nonnull ItemStack stack) {
    if (stack.getItem() != ModObject.itemDarkSteelBoots.getItemNN() || !EnergyUpgradeManager.itemHasAnyPowerUpgrade(stack)) {
      return false;
    }
    JumpUpgrade up = loadAnyFromItem(stack);
    if (up == null) {
      return getLevel() == 1;
    }
    return up.getLevel() == getLevel() - 1;
  }

  public short getLevel() {
    return level;
  }

}
