package crazypants.enderio.base.filter.redstone;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.DyeColor;

import crazypants.enderio.base.conduit.redstone.signals.BundledSignal;
import crazypants.enderio.base.conduit.redstone.signals.CombinedSignal;

public class DefaultOutputSignalFilter implements IOutputSignalFilter {

  public static final @Nonnull DefaultOutputSignalFilter instance = new DefaultOutputSignalFilter();

  private DefaultOutputSignalFilter() {
  }

  @Override
  @Nonnull
  public CombinedSignal apply(@Nonnull DyeColor color, @Nonnull BundledSignal bundledSignal) {
    return bundledSignal.getSignal(color);
  }

}
