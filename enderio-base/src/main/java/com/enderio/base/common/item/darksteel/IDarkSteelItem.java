package com.enderio.base.common.item.darksteel;

import com.enderio.base.client.renderer.DarkSteelDurabilityRenderer;
import com.enderio.base.common.capability.EIOCapabilities;
import com.enderio.base.common.capability.darksteel.DarkSteelUpgradeable;
import com.enderio.base.common.capability.darksteel.EnergyDelegator;
import com.enderio.base.common.capability.darksteel.IDarkSteelUpgrade;
import com.enderio.base.common.item.darksteel.upgrades.EmpoweredUpgrade;
import com.enderio.base.common.lang.EIOLang;
import com.enderio.core.client.render.IItemOverlayRender;
import com.enderio.core.common.capability.IMultiCapabilityItem;
import com.enderio.core.common.capability.INamedNBTSerializable;
import com.enderio.core.common.capability.MultiCapabilityProvider;
import com.enderio.core.common.util.EnergyUtil;
import com.enderio.core.common.util.TooltipUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;

import javax.annotation.Nullable;
import java.util.*;

public interface IDarkSteelItem extends IMultiCapabilityItem, IItemOverlayRender {

    default Optional<EmpoweredUpgrade> getEmpoweredUpgrade(ItemStack stack) {
        return DarkSteelUpgradeable.getUpgradeAs(stack, EmpoweredUpgrade.NAME, EmpoweredUpgrade.class);
    }

    default MultiCapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt, MultiCapabilityProvider provider) {
        return initDarkSteelCapabilities(provider, Objects.requireNonNull(stack.getItem().getRegistryName()));
    }

    default MultiCapabilityProvider initDarkSteelCapabilities(MultiCapabilityProvider provider, ResourceLocation forItem) {
        provider.addSerialized(EIOCapabilities.DARK_STEEL_UPGRADABLE, LazyOptional.of(() -> new DarkSteelUpgradeable(forItem)));
        provider.addSimple(CapabilityEnergy.ENERGY, LazyOptional.of(() -> new EnergyDelegator(provider)));
        return provider;
    }

    default void addCreativeItems(NonNullList<ItemStack> pItems, Item item) {
        ItemStack is = new ItemStack(item);
        pItems.add(is.copy());

        //All the upgrades
        is = new ItemStack(item);
        Collection<? extends IDarkSteelUpgrade> ups = DarkSteelUpgradeable.getAllPossibleUpgrades(is);
        for(IDarkSteelUpgrade upgrade : ups) {
            IDarkSteelUpgrade maxTier = upgrade;
            Optional<? extends IDarkSteelUpgrade> nextTier = maxTier.getNextTier();
            while(nextTier.isPresent()) {
                maxTier = nextTier.get();
                nextTier = maxTier.getNextTier();
            }
            DarkSteelUpgradeable.addUpgrade(is,maxTier);
        }
        EnergyUtil.setFull(is);
        pItems.add(is.copy());
    }

    default void addUpgradeHoverTest(ItemStack pStack, List<Component> pTooltipComponents) {
        //TODO: Only show when shift is held down
        // TODO: Move this bit into IEnergyBar
        if (DarkSteelUpgradeable.hasUpgrade(pStack, EmpoweredUpgrade.NAME)) {
            String energy = EnergyUtil.getEnergyStored(pStack) + "/" + EnergyUtil.getMaxEnergyStored(pStack);
            pTooltipComponents.add(new TranslatableComponent(EIOLang.ENERGY_AMOUNT.getKey(), energy));
        }

        // Get installed and available upgrades
        var upgrades = DarkSteelUpgradeable.getUpgrades(pStack);
        var availUpgrades = DarkSteelUpgradeable.getUpgradesApplicable(pStack);

        // Display installed upgrades
        upgrades
            .stream()
            .sorted(Comparator.comparing(INamedNBTSerializable::getSerializedName))
            .forEach(upgrade -> {
                pTooltipComponents.add(upgrade.getDisplayName());
                if (TooltipUtil.showExtended()) {
                    // TODO: Upgrade descriptions
                    pTooltipComponents.add(TooltipUtil.style(new TextComponent("Template for upgrade desc.")));
                }
            });

        // Show shift hint
        if (!upgrades.isEmpty() || !availUpgrades.isEmpty()) {
            TooltipUtil.showShiftHint(pTooltipComponents);
        }

        if(!availUpgrades.isEmpty() && TooltipUtil.showExtended()) {
            pTooltipComponents.add(EIOLang.DS_UPGRADE_AVAILABLE);
            availUpgrades
                .stream()
                .sorted(Comparator.comparing(INamedNBTSerializable::getSerializedName))
                .forEach(upgrade -> pTooltipComponents.add(new TextComponent("  ").append(upgrade.getDisplayName()).withStyle(ChatFormatting.ITALIC)));
        }
    }

    default void renderOverlay(ItemStack pStack, int pXPosition, int pYPosition) {
        DarkSteelDurabilityRenderer.renderOverlay(pStack, pXPosition, pYPosition);
    }

    @Override
    default boolean showDurabilityBar(ItemStack stack) {
        return stack.getDamageValue() > 0 || EnergyUtil.getMaxEnergyStored(stack) > 0;
    }

}