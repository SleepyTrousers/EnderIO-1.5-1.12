package crazypants.enderio.item.darksteel.upgrade;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;

import com.enderio.core.client.handlers.SpecialTooltipHandler;
import com.enderio.core.common.util.ItemUtil;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.EnderIO;

public abstract class AbstractUpgrade implements IDarkSteelUpgrade {

  public static final String KEY_LEVEL_COST = "level_cost";

  private static final String KEY_UNLOC_NAME = "unlocalized_name";

  public static final String KEY_UPGRADE_PREFIX = "enderio.darksteel.upgrade.";

  private static final String KEY_UPGRADE_ITEM = "upgradeItem";

  protected final int levelCost;
  protected final String id;
  protected final String unlocName;

  protected ItemStack upgradeItem;

  protected AbstractUpgrade(String id, String unlocName, ItemStack upgradeItem, int levelCost) {
    this.id = KEY_UPGRADE_PREFIX + id;
    this.unlocName = unlocName;
    this.upgradeItem = upgradeItem;
    this.levelCost = levelCost;
  }

  public AbstractUpgrade(String id, NBTTagCompound tag) {
    this.id = KEY_UPGRADE_PREFIX + id;
    levelCost = tag.getInteger(KEY_LEVEL_COST);
    unlocName = tag.getString(KEY_UNLOC_NAME);
    if(tag.hasKey(KEY_UPGRADE_ITEM)) {
      upgradeItem = ItemStack.loadItemStackFromNBT((NBTTagCompound) tag.getTag(KEY_UPGRADE_ITEM));
    }
  }

  @Override
  public boolean isUpgradeItem(ItemStack stack) {
    if(stack == null || stack.getItem() == null || getUpgradeItem() == null) {
      return false;
    }
    return stack.isItemEqual(getUpgradeItem()) && stack.stackSize == getUpgradeItem().stackSize;
  }

  @Override
  public ItemStack getUpgradeItem() {
    return upgradeItem;
  }
  
  @Override
  public String getUpgradeItemName() {
    return getUpgradeItem().getDisplayName();
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void addCommonEntries(ItemStack itemstack, EntityPlayer entityplayer, List list, boolean flag) {
    SpecialTooltipHandler.addCommonTooltipFromResources(list, getUnlocalizedName());
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void addBasicEntries(ItemStack itemstack, EntityPlayer entityplayer, List list, boolean flag) {
    list.add(EnumChatFormatting.DARK_AQUA + EnderIO.lang.localizeExact(getUnlocalizedName() + ".name"));
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void addDetailedEntries(ItemStack itemstack, EntityPlayer entityplayer, List list, boolean flag) {
    list.add(EnumChatFormatting.DARK_AQUA + EnderIO.lang.localizeExact(getUnlocalizedName() + ".name"));
    SpecialTooltipHandler.addDetailedTooltipFromResources(list, getUnlocalizedName());
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
  @SideOnly(Side.CLIENT)
  public IRenderUpgrade getRender() {
    return null;
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
    upgradeRoot.setString(KEY_UNLOC_NAME, getUnlocalizedName());

    if(getUpgradeItem() != null) {
      NBTTagCompound itemRoot = new NBTTagCompound();
      getUpgradeItem().writeToNBT(itemRoot);
      upgradeRoot.setTag(KEY_UPGRADE_ITEM, itemRoot);
    }

    writeUpgradeToNBT(upgradeRoot);

    NBTTagCompound stackRoot = ItemUtil.getOrCreateNBT(stack);
    stackRoot.setTag(id, upgradeRoot);
    stack.setTagCompound(stackRoot);
  }

  public NBTTagCompound getUpgradeRoot(ItemStack stack) {
    if(!hasUpgrade(stack)) {
      return null;
    }
    return (NBTTagCompound) stack.stackTagCompound.getTag(id);
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
