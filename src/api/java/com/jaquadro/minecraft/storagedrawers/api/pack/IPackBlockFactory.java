/*package com.jaquadro.minecraft.storagedrawers.api.pack;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;

public interface IPackBlockFactory
{
    Block createBlock (BlockConfiguration blockConfig, IPackDataResolver dataResolver);

    /**
     * Registers a factory-produced block with an appropriate item class.
     */
    //void registerBlock (Block block, String name);

    /**
     * Associates a sorting variant of a block with its corresponding basic block.
     */
    //void bindSortingBlock (Block basicBlock, Block sortingBlock);

    /**
     * Hides block from NEI if NEI is active.
     */
    //void hideBlock (String blockID);

    /**
     * Registers block metadata from an initialized DataResolver with Storage Drawers.
     */
    //void registerResolver (IExtendedDataResolver resolver);
//}
