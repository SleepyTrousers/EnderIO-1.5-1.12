package crazypants.enderio.base.filter.redstone;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.DyeColor;

import crazypants.enderio.base.conduit.redstone.signals.BundledSignal;
import crazypants.enderio.base.conduit.redstone.signals.Signal;

/**
 * A filter that can be added to a redstone conduit to filter its output
 *
 */
public interface IOutputSignalFilter extends IRedstoneSignalFilter {

  default @Nonnull Signal apply(@Nonnull DyeColor color, @Nonnull BundledSignal bundledSignal) {
    return Signal.NONE;
  }

}