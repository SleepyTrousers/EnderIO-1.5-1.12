package com.tterrag.registrate.util.nullness;

import java.util.Objects;

@FunctionalInterface
public interface NonNullUnaryOperator<T> extends NonNullFunction<T, T> {

    static <T> NonNullUnaryOperator<T> identity() {
        return t -> t;
    }

    default <V> NonNullUnaryOperator<T> andThen(NonNullUnaryOperator<T> after) {
        Objects.requireNonNull(after);
        return t -> after.apply(apply(t));
    }
}
