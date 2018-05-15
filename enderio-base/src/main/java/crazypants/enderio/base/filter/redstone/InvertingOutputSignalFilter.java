package crazypants.enderio.base.filter.redstone;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.DyeColor;

import crazypants.enderio.base.conduit.redstone.signals.BundledSignal;
import crazypants.enderio.base.conduit.redstone.signals.CombinedSignal;
import crazypants.enderio.base.conduit.redstone.signals.Signal;

public class InvertingOutputSignalFilter implements IOutputSignalFilter {

  @Override
  @Nonnull
  public Signal apply(@Nonnull DyeColor color, @Nonnull BundledSignal bundledSignal) {
    return bundledSignal.getSignal(color).getStrength() > 0 ? new Signal(CombinedSignal.NONE, -1) : new Signal(CombinedSignal.MAX, -2);
  }

  @Override
  public boolean hasGui() {
    return false;
  }
}
