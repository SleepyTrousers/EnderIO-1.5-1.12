package crazypants.enderio.base.filter.redstone;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.DyeColor;

import crazypants.enderio.base.conduit.redstone.signals.BundledSignal;
import crazypants.enderio.base.conduit.redstone.signals.Signal;

public class DefaultOutputSignalFilter implements IOutputSignalFilter {

  @Override
  @Nonnull
  public Signal apply(@Nonnull DyeColor color, @Nonnull BundledSignal bundledSignal) {
    return bundledSignal.getSignal(color);
  }

}
