package crazypants.enderio.item.darksteel.upgrade.jump;

import crazypants.enderio.config.Config;
import crazypants.enderio.handler.darksteel.AbstractUpgrade;
import crazypants.enderio.item.darksteel.DarkSteelItems;
import crazypants.enderio.item.darksteel.upgrade.energy.EnergyUpgrade;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class JumpUpgrade extends AbstractUpgrade {

  private static final String KEY_LEVEL = "level";

  private static String UPGRADE_NAME = "jumpBoost";

  public static JumpUpgrade JUMP_ONE = new JumpUpgrade("enderio.darksteel.upgrade.jump_one", 1, Config.darkSteelJumpOneCost);
  public static JumpUpgrade JUMP_TWO = new JumpUpgrade("enderio.darksteel.upgrade.jump_two", 2, Config.darkSteelJumpTwoCost);
  public static JumpUpgrade JUMP_THREE = new JumpUpgrade("enderio.darksteel.upgrade.jump_three", 3, Config.darkSteelJumpThreeCost);

  private short level;

  public static boolean isEquipped(EntityPlayer player) {
    ItemStack boots = player.getItemStackFromSlot(EntityEquipmentSlot.FEET);    
    return loadFromItem(boots) != null;
  }
  
  public static JumpUpgrade loadFromItem(ItemStack stack) {
    if(stack == null) {
      return null;
    }
    if(stack.getTagCompound() == null) {
      return null;
    }
    if(!stack.getTagCompound().hasKey(KEY_UPGRADE_PREFIX + UPGRADE_NAME)) {
      return null;
    }
    return new JumpUpgrade((NBTTagCompound) stack.getTagCompound().getTag(KEY_UPGRADE_PREFIX + UPGRADE_NAME));
  }

  public JumpUpgrade(NBTTagCompound tag) {
    super(UPGRADE_NAME, tag);
    this.level = tag.getShort(KEY_LEVEL);
  }

  public JumpUpgrade(String unlocName, int level, int levelCost) {
    super(UPGRADE_NAME, unlocName, new ItemStack(Blocks.PISTON), levelCost);
    this.level = (short) level;
  }

  @Override
  public boolean canAddToItem(ItemStack stack) {
    if(stack == null || stack.getItem() != ModObject.itemDarkSteelBoots || !EnergyUpgrade.itemHasAnyPowerUpgrade(stack)) {
      return false;
    }
    JumpUpgrade up = loadFromItem(stack);
    if(up == null) {
      return getLevel() == 1;
    }
    return up.getLevel() == getLevel() - 1;
  }


  @Override
  public boolean hasUpgrade(ItemStack stack) {
    if(!super.hasUpgrade(stack)) {
      return false;
    }
    JumpUpgrade up = loadFromItem(stack);
    if(up == null) {
      return false;
    }
    return up.unlocName.equals(unlocName);
  }

  @Override
  public void writeUpgradeToNBT(NBTTagCompound upgradeRoot) {
    upgradeRoot.setShort(KEY_LEVEL, getLevel());
  }

  public short getLevel() {
    return level;
  }
}
