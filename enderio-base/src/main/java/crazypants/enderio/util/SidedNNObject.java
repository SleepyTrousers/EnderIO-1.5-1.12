package crazypants.enderio.util;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NullHelper;

import net.minecraft.util.EnumFacing;

public class SidedNNObject<T> {

  @SuppressWarnings("unchecked")
  private final @Nonnull T[] data = (T[]) new Object[6];
  private final @Nonnull T nullValue;

  public SidedNNObject(@Nonnull T nullValue) {
    this.nullValue = nullValue;
  }

  public @Nonnull T get(@Nonnull EnumFacing side) {
    return NullHelper.first(data[side.ordinal()], nullValue);
  }

  /**
   * Set a new value and return it (for chaining).
   */
  public @Nonnull T set(@Nonnull EnumFacing side, T value) {
    return NullHelper.first(data[side.ordinal()] = value, nullValue);
  }

}
