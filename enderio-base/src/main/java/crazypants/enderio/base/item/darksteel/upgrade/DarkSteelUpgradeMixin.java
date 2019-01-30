package crazypants.enderio.base.item.darksteel.upgrade;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.MappedCapabilityProvider;
import com.enderio.core.common.transform.SimpleMixin;
import com.google.common.collect.Multimap;

import crazypants.enderio.api.upgrades.IDarkSteelItem;
import crazypants.enderio.api.upgrades.IDarkSteelUpgrade;
import crazypants.enderio.base.handler.darksteel.UpgradeRegistry;
import crazypants.enderio.base.item.darksteel.upgrade.energy.EnergyUpgradeCap;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.energy.CapabilityEnergy;

@SimpleMixin(IDarkSteelItem.class)
public abstract class DarkSteelUpgradeMixin extends Item implements IDarkSteelItem {

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

}
