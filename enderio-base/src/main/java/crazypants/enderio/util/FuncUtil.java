package crazypants.enderio.util;

import java.util.function.Consumer;
import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.util.NullHelper;

public final class FuncUtil {

  public interface FunctionNN<T, R> extends Function<T, R> {

    @Override
    R apply(@SuppressWarnings("null") @Nonnull T t);

  }

  public static <F, E> E runIf(@Nullable F source, FunctionNN<F, E> getter) {
    return source == null ? null : getter.apply(source);
  }

  public static <F, E> E runIf(@Nullable F source, FunctionNN<F, E> getter, E defaultValue) {
    return source == null ? defaultValue : getter.apply(source);
  }

  public static @Nonnull <F, E> E runIfNN(@Nullable F source, FunctionNN<F, E> getter, @Nonnull E defaultValue) {
    return NullHelper.first(source == null ? null : getter.apply(source), defaultValue);
  }

  public static <F> void doIf(@Nullable F source, Consumer<F> setter) {
    if (source != null) {
      setter.accept(source);
    }
  }

}
