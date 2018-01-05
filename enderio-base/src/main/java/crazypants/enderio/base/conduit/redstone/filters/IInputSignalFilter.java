package crazypants.enderio.base.conduit.redstone.filters;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.DyeColor;

import crazypants.enderio.base.conduit.redstone.signals.CombinedSignal;
import crazypants.enderio.base.conduit.redstone.signals.ISignalSource;
import crazypants.enderio.base.conduit.redstone.signals.Signal;

/**
 * A filter that can be added to a redstone conduit to filter its input
 *
 */
public interface IInputSignalFilter {

  default @Nonnull Signal apply(@Nonnull CombinedSignal signal, @Nonnull DyeColor color, @Nonnull ISignalSource source) {
    return new Signal(signal, color, source);
  }

}