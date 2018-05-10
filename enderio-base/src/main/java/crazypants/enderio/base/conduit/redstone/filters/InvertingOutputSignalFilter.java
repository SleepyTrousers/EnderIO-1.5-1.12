package crazypants.enderio.base.conduit.redstone.filters;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.DyeColor;

import crazypants.enderio.base.conduit.redstone.signals.BundledSignal;
import crazypants.enderio.base.conduit.redstone.signals.Signal;

public class InvertingOutputSignalFilter implements IOutputSignalFilter {

  // Just an example

  @Override
  @Nonnull
  public Signal apply(@Nonnull DyeColor color, @Nonnull BundledSignal bundledSignal) {
    return bundledSignal.getSignal(color).getStrength() > 0 ? Signal.NONE : Signal.MAX;
  }

}
