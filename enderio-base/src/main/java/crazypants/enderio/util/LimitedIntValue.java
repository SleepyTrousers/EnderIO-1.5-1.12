package crazypants.enderio.util;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import info.loenwind.autoconfig.factory.IValue;

public class LimitedIntValue implements IValue<Integer> {

  private final @Nonnull IValue<Integer> parent, loLimit, hiLimit;

  public LimitedIntValue(@Nonnull IValue<Integer> parent, @Nullable IValue<Integer> loLimit, @Nullable IValue<Integer> hiLimit) {
    this.parent = parent;
    this.loLimit = loLimit != null ? loLimit : () -> Integer.MIN_VALUE;
    this.hiLimit = hiLimit != null ? hiLimit : () -> Integer.MAX_VALUE;
  }

  @Override
  public @Nonnull Integer get() {
    return Math.max(Math.min(parent.get(), hiLimit.get()), loLimit.get());
  }

}
