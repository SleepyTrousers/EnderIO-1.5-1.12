package crazypants.enderio.base.conduit.redstone.signals;

import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.DyeColor;

import crazypants.enderio.base.filter.redstone.IOutputSignalFilter;

public class BundledSignal {

  private final @Nonnull Map<DyeColor, Signal> bundle;

  public BundledSignal() {
    bundle = new EnumMap<DyeColor, Signal>(DyeColor.class);
    for (DyeColor color : DyeColor.values()) {
      bundle.put(color, new Signal(Signal.NONE));
    }
  }

  @SuppressWarnings("null")
  @Nonnull
  public Signal getSignal(@Nonnull DyeColor color) {
    Signal result = Signal.NONE;
    Signal signal = bundle.get(color);
    if (signal != null && signal.getStrength() > result.getStrength()) {
      result = signal;
    }
    return result;
  }

  public void add(@Nonnull DyeColor color, int str) {
    bundle.get(color).addStrength(str);
  }

  public void remove(@Nonnull DyeColor color, int str) {
    bundle.get(color).removeStrength(str);
  }

  public void set(@Nonnull DyeColor color, @Nonnull Signal signal) {
    bundle.put(color, signal);
  }

  public void reset(@Nonnull DyeColor color) {
    bundle.remove(color);
  }

  public void clear() {
    for (Signal sig : bundle.values()) {
      sig.resetSignal();
    }
  }

  @Nonnull
  public Signal get(@Nonnull DyeColor color, @Nonnull IOutputSignalFilter filter) {
    return filter.apply(color, this);
  }

  @Nonnull
  public Collection<Signal> getSignals() {
    return bundle.values();
  }

}
