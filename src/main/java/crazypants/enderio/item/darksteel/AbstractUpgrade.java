package crazypants.enderio.item.darksteel;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.util.ItemUtil;
import crazypants.util.Lang;

public abstract class AbstractUpgrade implements IDarkSteelUpgrade {

  public static final String KEY_LEVEL_COST = "level_cost";

  private static final String KEY_UNLOC_NAME = "unlocalized_name";

  public static final String KEY_UPGRADE_PREFIX = "eio.darksteel.upgrade.";

  protected final int levelCost;
  protected final String id;
  protected final String unlocName;

  protected AbstractUpgrade(String id, String unlocName, int levelCost) {
    this.levelCost = levelCost;
    this.id =  KEY_UPGRADE_PREFIX + id;
    this.unlocName = unlocName;
  }

  public AbstractUpgrade(String id, NBTTagCompound tag) {
    this.id = KEY_UPGRADE_PREFIX + id;
    levelCost = tag.getInteger(KEY_LEVEL_COST);
    unlocName = tag.getString(KEY_UNLOC_NAME);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void addCommonEntries(ItemStack itemstack, EntityPlayer entityplayer, List list, boolean flag) {
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void addBasicEntries(ItemStack itemstack, EntityPlayer entityplayer, List list, boolean flag) {
    list.add(Lang.localize(unlocName));
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void addAdvancedEntries(ItemStack itemstack, EntityPlayer entityplayer, List list, boolean flag) {
  }

  @Override
  public int getLevelCost() {
    return levelCost;
  }

  @Override
  public String getUnlocalizedName() {
    return unlocName;
  }

  @Override
  public boolean hasUpgrade(ItemStack stack) {
    if(stack == null) {
      return false;
    }
    if(stack.stackTagCompound == null) {
      return false;
    }
    return stack.stackTagCompound.hasKey(id);
  }

  @Override
  public void writeToItem(ItemStack stack) {
    if(stack == null) {
      return;
    }
    NBTTagCompound upgradeRoot = new NBTTagCompound();
    upgradeRoot.setInteger(KEY_LEVEL_COST, levelCost);
    upgradeRoot.setString(KEY_UNLOC_NAME, unlocName);

    writeUpgradeToNBT(upgradeRoot);

    NBTTagCompound stackRoot = ItemUtil.getOrCreateNBT(stack);
    stackRoot.setTag(id, upgradeRoot);
    stack.setTagCompound(stackRoot);
  }

  public NBTTagCompound getUpgradeRoot(ItemStack stack) {
    if(!hasUpgrade(stack)) {
      return null;
    }
    return (NBTTagCompound)stack.stackTagCompound.getTag(id);
  }

  public abstract void writeUpgradeToNBT(NBTTagCompound upgradeRoot);

  @Override
  public void removeFromItem(ItemStack stack) {
    if(stack == null) {
      return;
    }
    if(stack.stackTagCompound == null) {
      return;
    }
    stack.stackTagCompound.removeTag(id);
  }

}
