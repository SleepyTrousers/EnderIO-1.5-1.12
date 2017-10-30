package crazypants.enderio.conduit.redstone.filters;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.util.DyeColor;

import crazypants.enderio.conduit.redstone.signals.BundledSignal;
import crazypants.enderio.conduit.redstone.signals.CombinedSignal;
import crazypants.enderio.conduit.redstone.signals.ISignalSource;

public class InvertingOutputSignalFilter implements IOutputSignalFilter {

  // Just an example

  @Override
  @Nonnull
  public CombinedSignal apply(@Nonnull DyeColor color, @Nonnull BundledSignal bundledSignal, @Nullable ISignalSource ignore) {
    return bundledSignal.get(color, ignore).getStrength() > 0 ? CombinedSignal.NONE : CombinedSignal.MAX;
  }

}
