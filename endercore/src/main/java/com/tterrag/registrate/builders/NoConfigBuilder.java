package com.tterrag.registrate.builders;

import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import com.tterrag.registrate.util.nullness.NonnullType;

import net.minecraftforge.registries.IForgeRegistryEntry;

public class NoConfigBuilder<R extends IForgeRegistryEntry<R>, T extends R, P> extends AbstractBuilder<R, T, P, NoConfigBuilder<R, T, P>> {
    
    private final NonNullSupplier<T> factory;

    public NoConfigBuilder(AbstractRegistrate<?> owner, P parent, String name, BuilderCallback callback, Class<? super R> registryType, NonNullSupplier<T> factory) {
        super(owner, parent, name, callback, registryType);
        this.factory = factory;
    }

    @Override
    protected @NonnullType T createEntry() {
        return factory.get();
    }
}
