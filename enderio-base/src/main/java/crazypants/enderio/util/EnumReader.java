package crazypants.enderio.util;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NullHelper;

public class EnumReader {

  public static @Nonnull <E extends Enum<E>> E get(Class<E> clazz, int idx) {
    E[] values = clazz.getEnumConstants();
    if (idx >= 0 && idx < values.length) {
      return NullHelper.first(values[idx], values[0]);
    }
    return NullHelper.first(values[0]);
  }

  public static <E extends Enum<E>> int put(E e) {
    return e.ordinal();
  }

}
