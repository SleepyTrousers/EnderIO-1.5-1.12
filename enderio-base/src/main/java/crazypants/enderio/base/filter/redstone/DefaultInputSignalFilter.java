package crazypants.enderio.base.filter.redstone;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.DyeColor;

import crazypants.enderio.base.conduit.redstone.signals.Signal;

/**
 * 
 * Default implementation of the input signal filter. Used to ensure Nonnull signals in redstone conduits
 *
 */
public class DefaultInputSignalFilter implements IInputSignalFilter {

  @Override
  @Nonnull
  public Signal apply(@Nonnull Signal signal, @Nonnull DyeColor color) {
    return signal;
  }

}
