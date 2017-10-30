package crazypants.enderio.conduit.redstone.signals;

import java.util.EnumMap;
import java.util.Map;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.DyeColor;
import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NNList.Callback;
import com.enderio.core.common.util.NullHelper;

public class BundledCombinedSignal {

  private final @Nonnull Map<DyeColor, CombinedSignal> bundle = new EnumMap<>(DyeColor.class);

  public BundledCombinedSignal() {
    NNList.of(DyeColor.class).apply(new Callback<DyeColor>() {
      @Override
      public void apply(@Nonnull DyeColor color) {
        bundle.put(color, CombinedSignal.NONE);
      }
    });
  }

  public @Nonnull CombinedSignal get(@Nonnull DyeColor color) {
    return NullHelper.first(bundle.get(color), CombinedSignal.NONE);
  }

  public void set(@Nonnull DyeColor color, @Nonnull CombinedSignal signal) {
    bundle.put(color, signal);
  }

  public void clear(@Nonnull DyeColor color) {
    bundle.put(color, CombinedSignal.NONE);
  }

  public void set(@Nonnull Signal signal) {
    bundle.put(signal.getColor(), signal);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    for (DyeColor color : DyeColor.values()) {
      result = prime * result + bundle.get(color).hashCode();
    }
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    BundledCombinedSignal other = (BundledCombinedSignal) obj;
    for (DyeColor color : DyeColor.values()) {
      if (!bundle.get(color).equals(other.bundle.get(color))) {
        return false;
      }
    }
    return true;
  }

}
