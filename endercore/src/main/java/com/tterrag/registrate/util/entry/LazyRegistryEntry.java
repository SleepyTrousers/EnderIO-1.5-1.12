package com.tterrag.registrate.util.entry;

import javax.annotation.Nullable;

import com.tterrag.registrate.util.nullness.NonNullSupplier;

public class LazyRegistryEntry<T> implements NonNullSupplier<T> {
    
    @Nullable
    private NonNullSupplier<? extends RegistryEntry<? extends T>> supplier;
    @Nullable
    private RegistryEntry<? extends T> value;

    public LazyRegistryEntry(NonNullSupplier<? extends RegistryEntry<? extends T>> supplier) {
        this.supplier = supplier;
    }
    
    @Override
    public T get() {
        NonNullSupplier<? extends RegistryEntry<? extends T>> supplier = this.supplier;
        if (supplier != null) {
            this.value = supplier.get();
            this.supplier = null;
        }
        return this.value.get();
    }
}
