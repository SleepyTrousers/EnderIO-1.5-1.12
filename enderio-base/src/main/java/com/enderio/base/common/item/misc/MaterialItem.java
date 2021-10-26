package com.enderio.base.common.item.misc;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;

public class MaterialItem extends Item {
    private final boolean hasGlint;

    public MaterialItem(Properties props, boolean hasGlint) {
        super(props);
        this.hasGlint = hasGlint;
    }

    @Override
    public boolean isFoil(@Nonnull ItemStack itemStack) {
        return hasGlint;
    }
}
