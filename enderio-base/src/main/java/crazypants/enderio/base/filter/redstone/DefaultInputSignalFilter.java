package crazypants.enderio.base.filter.redstone;

import javax.annotation.Nonnull;

import crazypants.enderio.base.conduit.redstone.signals.CombinedSignal;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * 
 * Default implementation of the input signal filter. Used to ensure Nonnull signals in redstone conduits
 *
 */
public class DefaultInputSignalFilter implements IInputSignalFilter {

  @Override
  @Nonnull
  public CombinedSignal apply(@Nonnull CombinedSignal signal, @Nonnull World world, @Nonnull BlockPos pos) {
    return new CombinedSignal(signal.getStrength());
  }

}
