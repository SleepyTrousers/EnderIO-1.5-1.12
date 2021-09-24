package com.tterrag.registrate.util.nullness;

import java.util.Objects;
import java.util.function.Supplier;

@Deprecated
public interface NullableSupplier<@NullableType T> extends Supplier<T> {
    
    @Override
    T get();

    default T getNonNull() {
        return getNonNull(() -> "Unexpected null value from supplier");
    }
    
    default T getNonNull(NonNullSupplier<String> errorMsg) {
        T res = get();
        Objects.requireNonNull(res, errorMsg);
        return res;
    }
    
    default NonNullSupplier<T> asNonNull() {
        return () -> getNonNull();
    }
    
    default NonNullSupplier<T> asNonNull(NonNullSupplier<String> errorMsg) {
        return () -> getNonNull(errorMsg);
    }
}
