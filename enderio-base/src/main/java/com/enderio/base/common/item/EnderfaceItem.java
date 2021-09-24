package com.enderio.base.common.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;

public class EnderfaceItem extends Item {
    public EnderfaceItem(Properties props) {
        super(props);
    }

    @Override
    public boolean isFoil(@Nonnull ItemStack itemStack) {
        return true;
    }
}
