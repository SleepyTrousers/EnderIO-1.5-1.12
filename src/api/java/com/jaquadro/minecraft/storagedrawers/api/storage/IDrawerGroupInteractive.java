package com.jaquadro.minecraft.storagedrawers.api.storage;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public interface IDrawerGroupInteractive extends IDrawerGroup
{
    ItemStack takeItemsFromSlot (int slot, int count);

    int putItemsIntoSlot (int slot, ItemStack stack, int count);

    int interactPutCurrentItemIntoSlot (int slot, EntityPlayer player);

    int interactPutCurrentInventoryIntoSlot (int slot, EntityPlayer player);
}
