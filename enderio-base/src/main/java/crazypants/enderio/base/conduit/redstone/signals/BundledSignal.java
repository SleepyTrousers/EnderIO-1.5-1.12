package crazypants.enderio.base.conduit.redstone.signals;

import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.DyeColor;

import crazypants.enderio.base.filter.redstone.IOutputSignalFilter;

public class BundledSignal {

  private final @Nonnull Map<DyeColor, Signal> bundle;
  private final @Nonnull Map<DyeColor, Map<Integer, Signal>> bundleSignals;

  public BundledSignal() {
    bundle = new EnumMap<DyeColor, Signal>(DyeColor.class);
    bundleSignals = new EnumMap<DyeColor, Map<Integer, Signal>>(DyeColor.class);
    for (DyeColor color : DyeColor.values()) {
      bundle.put(color, new Signal(CombinedSignal.NONE, -1));
      bundleSignals.put(color, new HashMap<Integer, Signal>());
    }
  }

  @SuppressWarnings("null")
  @Nonnull
  public Signal getSignal(@Nonnull DyeColor color) {
    Signal result = bundle.get(color);
    return result;
  }

  public void addSignal(@Nonnull DyeColor color, @Nonnull Signal signal) {
    Map<Integer, Signal> signalMap = bundleSignals.get(color);
    if (!signalMap.containsKey(signal.getId())) {
      signalMap.put(signal.getId(), signal);
    } else if (signalMap.get(signal.getId()).getStrength() != signal.getStrength()) {
      signalMap.put(signal.getId(), signal);
    }

    int str = 0;
    for (Signal sig : signalMap.values()) {
      str += sig.getStrength();
      if (str >= 15) {
        str = 15;
        break;
      }
    }

    bundle.get(color).setStrength(str);
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
  public CombinedSignal getFilteredSignal(@Nonnull DyeColor color, @Nonnull IOutputSignalFilter filter) {
    return filter.apply(color, this);
  }

  @Nonnull
  public Collection<Signal> getSignals() {
    return bundle.values();
  }

}
