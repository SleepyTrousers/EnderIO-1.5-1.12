package crazypants.enderio.base.handler.darksteel;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.client.handlers.SpecialTooltipHandler;
import com.enderio.core.common.util.ItemUtil;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.util.Prep;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class AbstractUpgrade implements IDarkSteelUpgrade {

  public static final @Nonnull String KEY_LEVEL_COST = "level_cost";

  private static final @Nonnull String KEY_UNLOC_NAME = "unlocalized_name";

  public static final @Nonnull String KEY_UPGRADE_PREFIX = "enderio.darksteel.upgrade.";

  private static final @Nonnull String KEY_UPGRADE_ITEM = "upgradeItem";

  protected final int levelCost;
  protected final @Nonnull String id;
  protected final @Nonnull String unlocName;

  protected @Nonnull ItemStack upgradeItem;

  protected AbstractUpgrade(@Nonnull String id, @Nonnull String unlocName, @Nonnull ItemStack upgradeItem, int levelCost) {
    this.id = KEY_UPGRADE_PREFIX + id;
    this.unlocName = unlocName;
    this.upgradeItem = upgradeItem;
    this.levelCost = levelCost;
  }

  public AbstractUpgrade(@Nonnull String id, @Nonnull NBTTagCompound tag) {
    this.id = KEY_UPGRADE_PREFIX + id;
    this.levelCost = tag.getInteger(KEY_LEVEL_COST);
    this.unlocName = tag.getString(KEY_UNLOC_NAME);
    if (tag.hasKey(KEY_UPGRADE_ITEM)) {
      this.upgradeItem = new ItemStack((NBTTagCompound) tag.getTag(KEY_UPGRADE_ITEM));
    } else {
      this.upgradeItem = Prep.getEmpty();
    }
  }

  @Override
  public boolean isUpgradeItem(@Nonnull ItemStack stack) {
    return Prep.isValid(stack) && stack.isItemEqual(getUpgradeItem()) && stack.getCount() == getUpgradeItem().getCount();
  }

  @Override
  public @Nonnull ItemStack getUpgradeItem() {
    return upgradeItem;
  }

  @Override
  public @Nonnull String getUpgradeItemName() {
    return getUpgradeItem().getDisplayName();
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void addCommonEntries(@Nonnull ItemStack itemstack, @Nullable EntityPlayer entityplayer, @Nonnull List<String> list, boolean flag) {
    SpecialTooltipHandler.addCommonTooltipFromResources(list, getUnlocalizedName());
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void addBasicEntries(@Nonnull ItemStack itemstack, @Nullable EntityPlayer entityplayer, @Nonnull List<String> list, boolean flag) {
    list.add(TextFormatting.DARK_AQUA + EnderIO.lang.localizeExact(getUnlocalizedName() + ".name"));
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void addDetailedEntries(@Nonnull ItemStack itemstack, @Nullable EntityPlayer entityplayer, @Nonnull List<String> list, boolean flag) {
    list.add(TextFormatting.DARK_AQUA + EnderIO.lang.localizeExact(getUnlocalizedName() + ".name"));
    SpecialTooltipHandler.addDetailedTooltipFromResources(list, getUnlocalizedName());
  }

  @Override
  public int getLevelCost() {
    return levelCost;
  }

  @Override
  public @Nonnull String getUnlocalizedName() {
    return unlocName;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public IRenderUpgrade getRender() {
    return null;
  }

  @Override
  public boolean hasUpgrade(@Nonnull ItemStack stack) {
    final NBTTagCompound tagCompound = stack.getTagCompound();
    return tagCompound != null && tagCompound.hasKey(id);
  }

  @Override
  public void writeToItem(@Nonnull ItemStack stack) {
    NBTTagCompound upgradeRoot = new NBTTagCompound();
    upgradeRoot.setInteger(KEY_LEVEL_COST, getLevelCost());
    upgradeRoot.setString(KEY_UNLOC_NAME, getUnlocalizedName());

    NBTTagCompound itemRoot = new NBTTagCompound();
    getUpgradeItem().writeToNBT(itemRoot);
    upgradeRoot.setTag(KEY_UPGRADE_ITEM, itemRoot);

    writeUpgradeToNBT(upgradeRoot);

    NBTTagCompound stackRoot = ItemUtil.getOrCreateNBT(stack);
    stackRoot.setTag(id, upgradeRoot);
    stack.setTagCompound(stackRoot);
  }

  public NBTTagCompound getUpgradeRoot(@Nonnull ItemStack stack) {
    final NBTTagCompound tagCompound = stack.getTagCompound();
    if (tagCompound == null || !hasUpgrade(stack)) {
      return null;
    }
    return (NBTTagCompound) tagCompound.getTag(id);
  }

  public abstract void writeUpgradeToNBT(@Nonnull NBTTagCompound upgradeRoot);

  @Override
  public void removeFromItem(@Nonnull ItemStack stack) {
    final NBTTagCompound tagCompound = stack.getTagCompound();
    if (tagCompound != null) {
      tagCompound.removeTag(id);
    }
  }

}
