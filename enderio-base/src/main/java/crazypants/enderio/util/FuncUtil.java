package crazypants.enderio.util;

import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.util.NullHelper;

public final class FuncUtil {

  public static <F, E> E runIf(@Nullable F source, Function<F, E> getter) {
    return source == null ? null : getter.apply(source);
  }

  public static <F, E> E runIf(@Nullable F source, Function<F, E> getter, E defaultValue) {
    return source == null ? defaultValue : getter.apply(source);
  }

  public static @Nonnull <F, E> E runIfNN(@Nullable F source, Function<F, E> getter, @Nonnull E defaultValue) {
    return NullHelper.first(source == null ? null : getter.apply(source), defaultValue);
  }

}
