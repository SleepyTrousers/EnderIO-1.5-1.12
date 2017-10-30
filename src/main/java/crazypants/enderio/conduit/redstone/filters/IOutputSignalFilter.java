package crazypants.enderio.conduit.redstone.filters;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.util.DyeColor;

import crazypants.enderio.conduit.redstone.signals.BundledSignal;
import crazypants.enderio.conduit.redstone.signals.CombinedSignal;
import crazypants.enderio.conduit.redstone.signals.ISignalSource;

/**
 * A filter that can be added to a redstone conduit to filter its output
 *
 */
public interface IOutputSignalFilter {

  default @Nonnull CombinedSignal apply(@Nonnull DyeColor color, @Nonnull BundledSignal bundledSignal, @Nullable ISignalSource ignore) {
    return CombinedSignal.NONE;
  }

}