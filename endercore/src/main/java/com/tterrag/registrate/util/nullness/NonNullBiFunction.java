package com.tterrag.registrate.util.nullness;

import java.util.function.BiFunction;

@FunctionalInterface
public interface NonNullBiFunction<@NonnullType T, @NonnullType U, @NonnullType R> extends BiFunction<T, U, R> {
    
    @Override
    R apply(T t, U u);
}
