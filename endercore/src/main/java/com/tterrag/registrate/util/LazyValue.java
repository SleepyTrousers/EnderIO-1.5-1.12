package com.tterrag.registrate.util;

import com.google.common.base.Suppliers;

import java.util.function.Supplier;

// Temp fix for Registrate methods getting reobfed incorrectly
public class LazyValue<T> {
    private final Supplier<T> factory;

    public LazyValue(Supplier<T> p_13970_) {
        this.factory = Suppliers.memoize(p_13970_::get);
    }

    public T getValue() {
        return this.factory.get();
    }
}
