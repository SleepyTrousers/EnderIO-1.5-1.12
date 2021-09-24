package com.tterrag.registrate.util;

import com.tterrag.registrate.AbstractRegistrate;

import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.IForgeRegistryEntry;

/**
 * @deprecated use {@link com.tterrag.registrate.util.entry.RegistryEntry}
 */
@Deprecated
public class RegistryEntry<T extends IForgeRegistryEntry<? super T>> extends com.tterrag.registrate.util.entry.RegistryEntry<T> {
    
    private RegistryEntry(AbstractRegistrate<?> owner, RegistryObject<T> delegate) {
        super(owner, delegate);
    }
}
