package crazypants.enderio.item.darksteel;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import crazypants.enderio.EnderIO;
import crazypants.enderio.material.Material;

public class JumpUpgrade extends AbstractUpgrade {

  private static final String KEY_LEVEL = "level";

  private static String UPGRADE_NAME = "jumpBoost";


  public static JumpUpgrade JUMP_ONE = new JumpUpgrade("enderio.darksteel.upgrade.jump_one", 1, 15);
  public static JumpUpgrade JUMP_TWO = new JumpUpgrade("enderio.darksteel.upgrade.jump_two", 2, 20);
  public static JumpUpgrade JUMP_THREE = new JumpUpgrade("enderio.darksteel.upgrade.jump_three", 3, 30);

  protected short level;

  public static JumpUpgrade loadFromItem(ItemStack stack) {
    if(stack == null) {
      return null;
    }
    if(stack.stackTagCompound == null) {
      return null;
    }
    if(!stack.stackTagCompound.hasKey(KEY_UPGRADE_PREFIX + UPGRADE_NAME)) {
      return null;
    }
    return new JumpUpgrade((NBTTagCompound) stack.stackTagCompound.getTag(KEY_UPGRADE_PREFIX + UPGRADE_NAME));
  }

  public JumpUpgrade(NBTTagCompound tag) {
    super(UPGRADE_NAME, tag);
    level = tag.getShort(KEY_LEVEL);
  }

  public JumpUpgrade(String unlocName,int level, int levelCost) {
    super(UPGRADE_NAME, unlocName, new ItemStack(EnderIO.itemMaterial, 1, Material.PULSATING_CYSTAL.ordinal()), levelCost);
    this.level = (short)level;
  }

  @Override
  public boolean canAddToItem(ItemStack stack) {
    if(stack == null || stack.getItem() != EnderIO.itemDarkSteelBoots || !EnergyUpgrade.itemHasAnyPowerUpgrade(stack)) {
      return false;
    }
    JumpUpgrade up = loadFromItem(stack);
    if(up == null) {
      return level == 1;
    }
    return up.level == level - 1;
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
    upgradeRoot.setShort(KEY_LEVEL, level);
  }

}
