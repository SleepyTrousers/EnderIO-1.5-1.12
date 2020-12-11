package crazypants.enderio.util;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.util.NullHelper;

public final class FuncUtil {

  @FunctionalInterface
  public interface FunctionNN<T, R> extends Function<T, R> {

    @Override
    R apply(@SuppressWarnings("null") @Nonnull T t);

  }

  @FunctionalInterface
  public interface ConsumerNN<T> extends Consumer<T> {

    @Override
    void accept(@SuppressWarnings("null") @Nonnull T t);

  }

  public static <F, E> E runIf(@Nullable F source, FunctionNN<F, E> getter) {
    return source == null ? null : getter.apply(source);
  }

  public static <F, E> E runIfOr(@Nullable F source, FunctionNN<F, E> getter, E defaultValue) {
    return source == null ? defaultValue : getter.apply(source);
  }

  public static <F, E> E runIfOrSup(@Nullable F source, FunctionNN<F, E> getter, Supplier<E> defaultValue) {
    return source == null ? defaultValue.get() : getter.apply(source);
  }

  public static @Nonnull <F, E> E runIfOrNN(@Nullable F source, FunctionNN<F, E> getter, @Nonnull E defaultValue) {
    return NullHelper.first(source == null ? null : getter.apply(source), defaultValue);
  }

  public static @Nonnull <F, E> E runIfOrSupNN(@Nullable F source, FunctionNN<F, E> getter, @Nonnull Supplier<E> defaultValue) {
    return NullHelper.first(source == null ? null : getter.apply(source), defaultValue.get());
  }

  public static <F> void doIf(@Nullable F source, ConsumerNN<F> setter) {
    if (source != null) {
      setter.accept(source);
    }
  }

}
