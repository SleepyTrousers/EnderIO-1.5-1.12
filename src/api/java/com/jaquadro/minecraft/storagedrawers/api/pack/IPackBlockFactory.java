package com.jaquadro.minecraft.storagedrawers.api.pack;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;

public interface IPackBlockFactory
{
    Block createBlock (BlockConfiguration blockConfig, IPackDataResolver dataResolver);

    void registerBlock (Block block, String name);
}
