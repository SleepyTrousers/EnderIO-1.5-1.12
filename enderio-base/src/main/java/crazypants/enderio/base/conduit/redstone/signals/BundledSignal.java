package crazypants.enderio.base.conduit.redstone.signals;

import java.util.EnumMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.util.DyeColor;
import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NNList.Callback;

import crazypants.enderio.base.conduit.redstone.filters.IOutputSignalFilter;

public class BundledSignal {

  private final @Nonnull Map<DyeColor, NNList<Signal>> bundle = new EnumMap<>(DyeColor.class);

  public BundledSignal() {
    NNList.of(DyeColor.class).apply(new Callback<DyeColor>() {
      @Override
      public void apply(@Nonnull DyeColor color) {
        bundle.put(color, new NNList<>());
      }
    });
  }

  public @Nonnull NNList<Signal> get(@Nonnull DyeColor color) {
    return bundle.get(color).copy();
  }

  public void set(@Nonnull DyeColor color, @Nonnull NNList<Signal> signals) {
    bundle.put(color, signals.copy());
  }

  public void clear(@Nonnull DyeColor color) {
    bundle.get(color).clear();
  }

  public void add(@Nonnull Signal signal) {
    bundle.get(signal.getColor()).add(signal);
  }

  public @Nonnull CombinedSignal get(@Nonnull DyeColor color, @Nonnull IOutputSignalFilter filter, @Nullable ISignalSource ignore) {
    return filter.apply(color, this, ignore);
  }

  public @Nonnull CombinedSignal get(@Nonnull DyeColor color, @Nullable ISignalSource ignore) {
    CombinedSignal result = CombinedSignal.NONE;
    for (Signal signal : get(color)) {
      if (ignore == null || !ignore.getSource().equals(signal.getSource()) && !ignore.getDir().equals(signal.getDir())) {
        if (result.getStrength() < signal.getStrength()) {
          result = signal;
        }
      }
    }
    return result;
  }

  public @Nonnull BundledCombinedSignal get(@Nullable ISignalSource ignore) {
    BundledCombinedSignal result = new BundledCombinedSignal();
    NNList.of(DyeColor.class).apply(new Callback<DyeColor>() {
      @Override
      public void apply(@Nonnull DyeColor color) {
        for (Signal signal : get(color)) {
          if (ignore == null || !ignore.getSource().equals(signal.getSource()) && !ignore.getDir().equals(signal.getDir())) {
            if (result.get(color).getStrength() < signal.getStrength()) {
              result.set(signal);
            }
          }
        }
      }
    });
    return result;
  }

}
