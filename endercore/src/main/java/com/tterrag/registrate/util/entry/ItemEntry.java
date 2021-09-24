package com.tterrag.registrate.util.entry;

import com.tterrag.registrate.AbstractRegistrate;

import net.minecraft.world.item.Item;
import net.minecraftforge.fmllegacy.RegistryObject;

public class ItemEntry<T extends Item> extends ItemProviderEntry<T> {

    public ItemEntry(AbstractRegistrate<?> owner, RegistryObject<T> delegate) {
        super(owner, delegate);
    }
    
    public static <T extends Item> ItemEntry<T> cast(RegistryEntry<T> entry) {
        return RegistryEntry.cast(ItemEntry.class, entry);
    }
}
