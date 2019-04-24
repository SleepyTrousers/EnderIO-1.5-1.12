package crazypants.enderio.base.item.darksteel.upgrade;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.api.client.gui.IAdvancedTooltipProvider;
import com.enderio.core.common.MappedCapabilityProvider;
import com.enderio.core.common.mixin.SimpleMixin;
import com.google.common.collect.Multimap;

import crazypants.enderio.api.upgrades.IDarkSteelItem;
import crazypants.enderio.api.upgrades.IDarkSteelUpgrade;
import crazypants.enderio.base.handler.darksteel.DarkSteelRecipeManager;
import crazypants.enderio.base.handler.darksteel.UpgradeRegistry;
import crazypants.enderio.base.item.darksteel.upgrade.energy.EnergyUpgradeCap;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.energy.CapabilityEnergy;

@SimpleMixin(IDarkSteelItem.class)
public abstract class DarkSteelUpgradeMixin extends Item implements IDarkSteelItem, IAdvancedTooltipProvider {

  // Capabilities (Energy Upgrade)

  @Override
  public @Nullable ICapabilityProvider initCapabilities(@Nonnull ItemStack stack, @Nullable NBTTagCompound nbt) {
    return initCapabilities(stack, nbt, new MappedCapabilityProvider().add(CapabilityEnergy.ENERGY, new EnergyUpgradeCap(stack)));
  }

  // Attribute Modifiers

  @Override
  public @Nonnull Multimap<String, AttributeModifier> getAttributeModifiers(@Nonnull EntityEquipmentSlot slot, @Nonnull ItemStack stack) {
    final Multimap<String, AttributeModifier> map = super.getAttributeModifiers(slot, stack);
    for (IDarkSteelUpgrade upgrade : UpgradeRegistry.getUpgrades()) {
      if (upgrade.hasUpgrade(stack)) {
        upgrade.addAttributeModifiers(slot, stack, map);
      }
    }
    return map;
  }

  // Tooltips

  @Override
  public void addCommonEntries(@Nonnull ItemStack itemstack, @Nullable EntityPlayer entityplayer, @Nonnull List<String> list, boolean flag) {
    DarkSteelRecipeManager.addCommonTooltipEntries(itemstack, entityplayer, list, flag);
  }

  @Override
  public void addBasicEntries(@Nonnull ItemStack itemstack, @Nullable EntityPlayer entityplayer, @Nonnull List<String> list, boolean flag) {
    DarkSteelRecipeManager.addBasicTooltipEntries(itemstack, entityplayer, list, flag);
  }

  @Override
  public void addDetailedEntries(@Nonnull ItemStack itemstack, @Nullable EntityPlayer entityplayer, @Nonnull List<String> list, boolean flag) {
    DarkSteelRecipeManager.addAdvancedTooltipEntries(itemstack, entityplayer, list, flag);
  }

}
