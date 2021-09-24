package com.tterrag.registrate.providers;

import net.minecraft.data.DataProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistryEntry;

public interface RegistrateProviderDelegate<R extends IForgeRegistryEntry<R>, T extends R> extends DataProvider {
    
    String getName();
    
    ResourceLocation getId();
    
    T getEntry();
}