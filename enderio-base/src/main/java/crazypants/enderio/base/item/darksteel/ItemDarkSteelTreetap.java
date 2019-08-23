package crazypants.enderio.base.item.darksteel;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.api.client.gui.IAdvancedTooltipProvider;
import com.enderio.core.client.handlers.SpecialTooltipHandler;
import com.enderio.core.common.interfaces.IOverlayRenderAware;
import com.enderio.core.common.util.ItemUtil;
import com.enderio.core.common.util.OreDictionaryHelper;

import crazypants.enderio.api.IModObject;
import crazypants.enderio.api.capacitor.ICapacitorKey;
import crazypants.enderio.api.upgrades.IDarkSteelItem;
import crazypants.enderio.api.upgrades.IEquipmentData;
import crazypants.enderio.base.EnderIOTab;
import crazypants.enderio.base.capacitor.CapacitorKey;
import crazypants.enderio.base.config.config.DarkSteelConfig;
import crazypants.enderio.base.handler.darksteel.DarkSteelTooltipManager;
import crazypants.enderio.base.item.darksteel.attributes.EquipmentData;
import crazypants.enderio.base.item.darksteel.upgrade.energy.EnergyUpgrade;
import crazypants.enderio.base.item.darksteel.upgrade.energy.EnergyUpgrade.EnergyUpgradeHolder;
import crazypants.enderio.base.item.darksteel.upgrade.energy.EnergyUpgradeManager;
import crazypants.enderio.base.material.alloy.Alloy;
import crazypants.enderio.base.render.itemoverlay.PowerBarOverlayRenderHelper;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

public class ItemDarkSteelTreetap extends Item implements IAdvancedTooltipProvider, IDarkSteelItem, IOverlayRenderAware {

  public static ItemDarkSteelTreetap create(@Nonnull IModObject modObject, @Nullable Block block) {
    ItemDarkSteelTreetap res = new ItemDarkSteelTreetap(modObject);
    return res;
  }

  protected ItemDarkSteelTreetap(@Nonnull IModObject modObject) {
    this.setMaxDamage(DarkSteelConfig.tapDurability.get());
    setCreativeTab(EnderIOTab.tabEnderIOItems);
    modObject.apply(this);
  }

  @Override
  public int getIngotsRequiredForFullRepair() {
    return 3;
  }

  @Override
  public boolean isItemForRepair(@Nonnull ItemStack right) {
    return OreDictionaryHelper.hasName(right, Alloy.DARK_STEEL.getOreIngot());
  }

  @Override
  public void getSubItems(@Nonnull CreativeTabs tab, @Nonnull NonNullList<ItemStack> list) {
    if (isInCreativeTab(tab)) {
      ItemStack is = new ItemStack(this);
      list.add(is);

      is = new ItemStack(this);
      EnergyUpgrade.UPGRADES.get(3).addToItem(is, this);
      EnergyUpgradeManager.setPowerFull(is, this);
      list.add(is);
    }
  }

  @Override
  public void setDamage(@Nonnull ItemStack stack, int newDamage) {
    int oldDamage = getDamage(stack);
    if (newDamage <= oldDamage) {
      super.setDamage(stack, newDamage);
    } else {
      int damage = newDamage - oldDamage;

      if (!absorbDamageWithEnergy(stack, damage * DarkSteelConfig.tapEnergyPerDamage.get())) {
        super.setDamage(stack, newDamage);
      }
    }
  }

  private boolean absorbDamageWithEnergy(@Nonnull ItemStack stack, int amount) {
    EnergyUpgradeHolder eu = EnergyUpgradeManager.loadFromItem(stack);
    if (eu != null && eu.isAbsorbDamageWithPower() && eu.getEnergy() > 0) {
      eu.extractEnergy(amount, false);
      eu.writeToItem();
      return true;
    } else {
      return false;
    }
  }

  @Override
  public void addCommonEntries(@Nonnull ItemStack itemstack, @Nullable EntityPlayer entityplayer, @Nonnull List<String> list, boolean flag) {
    DarkSteelTooltipManager.addCommonTooltipEntries(itemstack, entityplayer, list, flag);
  }

  @Override
  public void addBasicEntries(@Nonnull ItemStack itemstack, @Nullable EntityPlayer entityplayer, @Nonnull List<String> list, boolean flag) {
    DarkSteelTooltipManager.addBasicTooltipEntries(itemstack, entityplayer, list, flag);
  }

  @Override
  public void addDetailedEntries(@Nonnull ItemStack itemstack, @Nullable EntityPlayer entityplayer, @Nonnull List<String> list, boolean flag) {
    if (!SpecialTooltipHandler.showDurability(flag)) {
      list.add(ItemUtil.getDurabilityString(itemstack));
    }
    String str = EnergyUpgradeManager.getStoredEnergyString(itemstack);
    if (str != null) {
      list.add(str);
    }
    DarkSteelTooltipManager.addAdvancedTooltipEntries(itemstack, entityplayer, list, flag);
  }

  @Override
  public void renderItemOverlayIntoGUI(@Nonnull ItemStack stack, int xPosition, int yPosition) {
    PowerBarOverlayRenderHelper.instance_upgradeable.render(stack, xPosition, yPosition);
  }

  @Override
  public boolean isForSlot(@Nonnull EntityEquipmentSlot slot) {
    return slot == EntityEquipmentSlot.MAINHAND;
  }

  @Override
  public @Nonnull IEquipmentData getEquipmentData() {
    return EquipmentData.DARK_STEEL;
  }

  @Override
  public @Nonnull ICapacitorKey getEnergyStorageKey(@Nonnull ItemStack stack) {
    return CapacitorKey.DARK_STEEL_TREETAP_ENERGY_BUFFER;
  }

  @Override
  public @Nonnull ICapacitorKey getEnergyInputKey(@Nonnull ItemStack stack) {
    return CapacitorKey.DARK_STEEL_TREETAP_ENERGY_INPUT;
  }

  @Override
  public @Nonnull ICapacitorKey getEnergyUseKey(@Nonnull ItemStack stack) {
    return CapacitorKey.DARK_STEEL_TREETAP_ENERGY_USE;
  }

  @Override
  public @Nonnull ICapacitorKey getAbsorptionRatioKey(@Nonnull ItemStack stack) {
    return CapacitorKey.DARK_STEEL_TREETAP_ABSORPTION_RATIO;
  }

  @Override
  public @Nonnull ActionResult<ItemStack> onItemRightClick(@Nonnull World worldIn, @Nonnull EntityPlayer playerIn, @Nonnull EnumHand handIn) {
    if (playerIn.isSneaking()) {
      if (!worldIn.isRemote) {
        openUpgradeGui(playerIn, handIn);
      }
      return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
    }
    return super.onItemRightClick(worldIn, playerIn, handIn);
  }

}
