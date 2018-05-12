package crazypants.enderio.base.filter.redstone;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.DyeColor;

import crazypants.enderio.base.conduit.redstone.signals.Signal;

/**
 * A filter that can be added to a redstone conduit to filter its input
 *
 */
public interface IInputSignalFilter extends IRedstoneSignalFilter {

  @Nonnull
  Signal apply(@Nonnull Signal signal, @Nonnull DyeColor color);

}