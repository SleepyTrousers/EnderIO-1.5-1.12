package crazypants.enderio.util;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;

import net.minecraft.util.EnumFacing;

public class SidedObject<T> {

  @SuppressWarnings("unchecked")
  private final @Nonnull T[] data = (T[]) new Object[6];

  public SidedObject() {
    this(null);
  }

  public SidedObject(T defaultValue) {
    NNList.FACING.apply((NNList.Callback<EnumFacing>) side -> set(side, defaultValue));
  }

  public T get(@Nonnull EnumFacing side) {
    return data[side.ordinal()];
  }

  /**
   * Set a new value and return it (for chaining).
   */
  public T set(@Nonnull EnumFacing side, T value) {
    return data[side.ordinal()] = value;
  }

}
