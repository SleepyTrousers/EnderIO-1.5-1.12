package com.enderio.core.common.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.extensions.IForgeItem;

import javax.annotation.Nullable;

/**
 * Implement for an item that should use the {@link MultiCapabilityProvider} when initializing capabilities.
 */
public interface IMultiCapabilityItem extends IForgeItem {
    @Nullable
    @Override
    default ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return initCapabilities(stack, nbt, new MultiCapabilityProvider());
    }

    @Nullable
    MultiCapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt, MultiCapabilityProvider provider);
}
