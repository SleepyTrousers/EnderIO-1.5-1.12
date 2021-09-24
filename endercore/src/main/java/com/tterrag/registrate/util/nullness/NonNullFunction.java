package com.tterrag.registrate.util.nullness;

import java.util.Objects;
import java.util.function.Function;

@FunctionalInterface
public interface NonNullFunction<@NonnullType T, @NonnullType R> extends Function<T, R> {

    @Override
    R apply(T t);

    default <V> NonNullFunction<T, V> andThen(NonNullFunction<? super R, ? extends V> after) {
        Objects.requireNonNull(after);
        return t -> after.apply(apply(t));
    }
}
