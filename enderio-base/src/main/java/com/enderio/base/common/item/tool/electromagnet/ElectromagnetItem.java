package com.enderio.base.common.item.tool.electromagnet;

import com.enderio.base.common.item.util.IEnergyBar;
import com.enderio.base.common.capability.EIOCapabilities;
import com.enderio.base.common.capability.toggled.IToggled;
import com.enderio.base.common.capability.toggled.Toggled;
import com.enderio.core.common.capability.MultiCapabilityProvider;
import com.enderio.core.common.capability.IMultiCapabilityItem;
import com.enderio.core.common.util.EnergyUtil;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.EnergyStorage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

// TODO: Behaviours
public class ElectromagnetItem extends Item implements IEnergyBar, IMultiCapabilityItem {
    public ElectromagnetItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public boolean isFoil(ItemStack pStack) {
        return pStack.getCapability(EIOCapabilities.TOGGLED).map(IToggled::isEnabled).orElse(false);
    }

    @Override
    public void fillItemCategory(@Nonnull CreativeModeTab pCategory, @Nonnull NonNullList<ItemStack> pItems) {
        if (allowdedIn(pCategory)) {
            ItemStack is = new ItemStack(this);
            pItems.add(is.copy());

            EnergyUtil.setFull(is);
            pItems.add(is);
        }
    }

    @Nullable
    @Override
    public MultiCapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt, MultiCapabilityProvider provider) {
        provider.addSerialized(EIOCapabilities.TOGGLED, LazyOptional.of(Toggled::new));
        provider.addSerialized("Energy", CapabilityEnergy.ENERGY, LazyOptional.of(() -> new EnergyStorage(1000)));
        return provider;
    }
}
