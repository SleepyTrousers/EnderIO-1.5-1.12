package crazypants.enderio.base.conduit.redstone.signals;

import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.DyeColor;

import crazypants.enderio.base.filter.redstone.IOutputSignalFilter;

public class BundledSignal {

  private final @Nonnull Map<DyeColor, CombinedSignal> bundle;
  private final @Nonnull Map<DyeColor, Map<Integer, Signal>> bundleSignals;

  public BundledSignal() {
    bundle = new EnumMap<>(DyeColor.class);
    bundleSignals = new EnumMap<>(DyeColor.class);
    for (DyeColor color : DyeColor.values()) {
      bundle.put(color, new CombinedSignal(0));
      bundleSignals.put(color, new HashMap<Integer, Signal>());
    }
  }

  @SuppressWarnings("null")
  @Nonnull
  public CombinedSignal getSignal(@Nonnull DyeColor color) {
    return bundle.get(color);
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

  public void set(@Nonnull DyeColor color, @Nonnull CombinedSignal signal) {
    bundle.put(color, signal);
  }

  public void reset(@Nonnull DyeColor color) {
    bundle.remove(color);
  }

  @Nonnull
  public CombinedSignal getFilteredSignal(@Nonnull DyeColor color, @Nonnull IOutputSignalFilter filter) {
    return filter.apply(color, this);
  }

  @Nonnull
  public Collection<CombinedSignal> getSignals() {
    return bundle.values();
  }

  public void clear() {
    for (CombinedSignal sig : bundle.values()) {
      sig.setStrength(0);
    }
  }

}
