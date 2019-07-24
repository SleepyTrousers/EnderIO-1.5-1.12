package crazypants.enderio.base.handler.darksteel;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Pair;

import com.enderio.core.api.client.gui.IAdvancedTooltipProvider;
import com.enderio.core.client.handlers.SpecialTooltipHandler;

import crazypants.enderio.api.upgrades.IDarkSteelItem;
import crazypants.enderio.api.upgrades.IDarkSteelUpgrade;
import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.init.ModObject;
import crazypants.enderio.base.material.upgrades.ItemUpgrades;
import crazypants.enderio.util.NbtValue;
import info.loenwind.autoconfig.factory.IValue;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistryEntry.Impl;

public abstract class AbstractUpgrade extends Impl<IDarkSteelUpgrade> implements IAdvancedTooltipProvider, IDarkSteelUpgrade {

  public static final @Nonnull String KEY_UPGRADE_PREFIX = "enderio.darksteel.upgrade.";

  private static final @Nonnull String KEY_VARIANT = "level";

  protected final int variant;
  protected final IValue<Integer> levelCost;
  protected final @Nonnull String id;
  protected final @Nonnull String unlocName;

  @Deprecated
  protected AbstractUpgrade(@Nonnull String id, @Nonnull String unlocName, int levelCost) {
    this(id, 0, unlocName, levelCost);
  }

  @Deprecated
  protected AbstractUpgrade(@Nonnull String id, int variant, @Nonnull String unlocName, int levelCost) {
    this(EnderIO.DOMAIN, id, variant, unlocName, levelCost);
  }

  @Deprecated
  protected AbstractUpgrade(@Nonnull String domain, @Nonnull String id, int variant, @Nonnull String unlocName, int levelCost) {
    this(domain, id, variant, unlocName, new IValue<Integer>() {
      @Override
      @Nonnull
      public Integer get() {
        return levelCost;
      }
    });
  }

  protected AbstractUpgrade(@Nonnull String id, @Nonnull String unlocName, IValue<Integer> levelCost) {
    this(id, 0, unlocName, levelCost);
  }

  protected AbstractUpgrade(@Nonnull String domain, @Nonnull String id, @Nonnull String unlocName, IValue<Integer> levelCost) {
    this(domain, id, 0, unlocName, levelCost);
  }

  protected AbstractUpgrade(@Nonnull String id, int variant, @Nonnull String unlocName, IValue<Integer> levelCost) {
    this(EnderIO.DOMAIN, id, variant, unlocName, levelCost);
  }

  protected AbstractUpgrade(@Nonnull String domain, @Nonnull String id, int variant, @Nonnull String unlocName, IValue<Integer> levelCost) {
    this.id = KEY_UPGRADE_PREFIX + id;
    this.unlocName = unlocName;
    this.levelCost = levelCost;
    this.variant = variant;
    setRegistryName(domain, id + (variant != 0 ? variant : ""));
  }

  @Override
  public final @Nonnull ItemStack getUpgradeItem() {
    return ((ItemUpgrades) ModObject.itemDarkSteelUpgrade.getItemNN()).withUpgrade(this);
  }

  @Override
  public final boolean isUpgradeItem(@Nonnull ItemStack stack) {
    return ((ItemUpgrades) ModObject.itemDarkSteelUpgrade.getItemNN()).getUpgrade(stack) == this;
  }

  @Override
  final public @Nonnull String getUpgradeItemName() {
    return IDarkSteelUpgrade.super.getUpgradeItemName();
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
    return levelCost.get();
  }

  @Override
  public @Nonnull String getUnlocalizedName() {
    return unlocName;
  }

  @Override
  public final boolean hasUpgrade(@Nonnull ItemStack stack) {
    return IDarkSteelUpgrade.super.hasUpgrade(stack);
  }

  @Override
  public boolean hasUpgrade(@Nonnull ItemStack stack, @Nonnull IDarkSteelItem item) {
    final NBTTagCompound tagCompound = NbtValue.getReadOnlyRoot(stack);
    return tagCompound.hasKey(id) && tagCompound.getCompoundTag(id).getInteger(KEY_VARIANT) == variant;
  }

  public boolean hasAnyUpgradeVariant(@Nonnull ItemStack stack) {
    final NBTTagCompound tagCompound = NbtValue.getReadOnlyRoot(stack);
    return tagCompound.hasKey(id) && tagCompound.getCompoundTag(id).hasKey(KEY_VARIANT);
  }

  /**
   * If the given item has any level of this upgrade, returns the level. If not, return -1.
   */
  public int getUpgradeVariantLevel(@Nonnull ItemStack stack) {
    final NBTTagCompound tagCompound = NbtValue.getReadOnlyRoot(stack);
    return tagCompound.hasKey(id) && tagCompound.getCompoundTag(id).hasKey(KEY_VARIANT) ? tagCompound.getCompoundTag(id).getInteger(KEY_VARIANT) : -1;
  }

  @Override
  public void addToItem(@Nonnull ItemStack stack, @Nonnull IDarkSteelItem item) {
    getOrCreateUpgradeNBT(stack).setInteger(KEY_VARIANT, variant);
  }

  /**
   * TODO 1.13: Make it so that this method can be eliminated because all upgrades start at level 0!
   */
  protected int getMinVariant() {
    return 0;
  }

  @Override
  public @Nonnull Pair<ItemStack, Integer> removeFromItem(@Nonnull ItemStack stack, @Nonnull IDarkSteelItem item) {
    if (variant > getMinVariant()) {
      getOrCreateUpgradeNBT(stack).setInteger(KEY_VARIANT, variant - 1);
    } else {
      NBTTagCompound tagCompound = NbtValue.getOrCreateRoot(stack);
      if (tagCompound.hasKey(id)) {
        tagCompound.removeTag(id);
      }
      if (tagCompound.hasNoTags()) {
        stack.setTagCompound(null);
      }
    }
    return Pair.of(getUpgradeItem(), getLevelCost());
  }

  public @Nonnull NBTTagCompound getOrCreateUpgradeNBT(@Nonnull ItemStack stack) {
    NBTTagCompound tagCompound = NbtValue.getOrCreateRoot(stack);
    if (!tagCompound.hasKey(id)) {
      tagCompound.setTag(id, new NBTTagCompound());
    }
    return tagCompound.getCompoundTag(id);
  }

}
