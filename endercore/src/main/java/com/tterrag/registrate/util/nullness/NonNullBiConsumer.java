package com.tterrag.registrate.util.nullness;

import java.util.function.BiConsumer;

@FunctionalInterface
public interface NonNullBiConsumer<@NonnullType T, @NonnullType U> extends BiConsumer<T, U> {
    
    @Override
    void accept(T t, U u);

    static <T, U> NonNullBiConsumer<T, U> noop() {
        return (t, u) -> {};
    }
}
