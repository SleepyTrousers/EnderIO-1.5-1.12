package com.jaquadro.minecraft.storagedrawers.api.inventory;

import net.minecraft.item.ItemStack;

public interface IInventoryAdapter
{
    ItemStack getInventoryStack (SlotType slotType);

    void setInStack (ItemStack stack);

    void setOutStack (ItemStack stack);

    void syncInventory ();

    boolean syncInventoryIfNeeded ();
}
