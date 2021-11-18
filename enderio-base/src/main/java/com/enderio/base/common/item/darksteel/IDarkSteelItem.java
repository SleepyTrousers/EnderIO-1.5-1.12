package com.enderio.base.common.item.darksteel;

import com.enderio.base.common.capability.EIOCapabilities;
import com.enderio.base.common.capability.darksteel.DarkSteelUpgradeable;
import com.enderio.base.common.capability.darksteel.EnergyDelegator;
import com.enderio.base.common.capability.darksteel.IDarkSteelUpgrade;
import com.enderio.base.common.item.darksteel.upgrades.EmpoweredUpgrade;
import com.enderio.base.common.lang.EIOLang;
import com.enderio.core.common.capability.IMultiCapabilityItem;
import com.enderio.core.common.capability.INamedNBTSerializable;
import com.enderio.core.common.capability.MultiCapabilityProvider;
import com.enderio.core.common.util.EnergyUtil;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;

import javax.annotation.Nullable;
import java.util.*;

public interface IDarkSteelItem extends IMultiCapabilityItem {

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
        if (DarkSteelUpgradeable.hasUpgrade(pStack, EmpoweredUpgrade.NAME)) {
            String energy = EnergyUtil.getEnergyStored(pStack) + "/" + EnergyUtil.getMaxEnergyStored(pStack);
            pTooltipComponents.add(new TextComponent(energy));
        }

        var upgrades = DarkSteelUpgradeable.getUpgrades(pStack);
        upgrades
            .stream()
            .sorted(Comparator.comparing(INamedNBTSerializable::getSerializedName))
            .forEach(upgrade -> pTooltipComponents.add(upgrade.getDisplayName()));

        var availUpgrades = DarkSteelUpgradeable.getUpgradesThatCanBeAppliedAtTheMoment(pStack);
        if(!availUpgrades.isEmpty()) {
            pTooltipComponents.add(TextComponent.EMPTY);
            pTooltipComponents.add(EIOLang.DS_UPGRADE_AVAILABLE);
            availUpgrades
                .stream()
                .sorted(Comparator.comparing(INamedNBTSerializable::getSerializedName))
                .forEach(upgrade -> pTooltipComponents.add(upgrade.getDisplayName()));
        }
    }

}
