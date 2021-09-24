package com.tterrag.registrate.util.nullness;

import java.util.Objects;
import java.util.function.Consumer;

@FunctionalInterface
public interface NonNullConsumer<@NonnullType T> extends Consumer<T> {
    
    @Override
    void accept(T t);
    
    default NonNullConsumer<T> andThen(NonNullConsumer<? super T> after) {
        Objects.requireNonNull(after);
        return (T t) -> { accept(t); after.accept(t); };
    }
    
    static <T> NonNullConsumer<T> noop() {
        return t -> {};
    }
}
