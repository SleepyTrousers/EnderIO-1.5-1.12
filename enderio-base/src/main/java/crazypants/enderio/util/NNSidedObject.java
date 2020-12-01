package crazypants.enderio.util;

import java.util.function.Function;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NullHelper;

import net.minecraft.util.EnumFacing;

public class NNSidedObject<T> {

  @SuppressWarnings("unchecked")
  private final @Nonnull T[] data = (T[]) new Object[6];

  public NNSidedObject(@Nonnull Function<EnumFacing, T> defaultProvider) {
    NNList.FACING.apply((NNList.Callback<EnumFacing>) side -> set(side, NullHelper.notnull(defaultProvider.apply(side), "internal logic error")));
  }

  public NNSidedObject(@Nonnull T defaultValue) {
    NNList.FACING.apply((NNList.Callback<EnumFacing>) side -> set(side, defaultValue));
  }

  public @Nonnull T get(@Nonnull EnumFacing side) {
    return NullHelper.notnull(data[side.ordinal()], "internal logic error");
  }

  /**
   * Set a new value and return it (for chaining).
   */
  public T set(@Nonnull EnumFacing side, @Nonnull T value) {
    return data[side.ordinal()] = value;
  }

}
