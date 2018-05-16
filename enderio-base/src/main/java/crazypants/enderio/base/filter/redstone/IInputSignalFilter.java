package crazypants.enderio.base.filter.redstone;

import javax.annotation.Nonnull;

import crazypants.enderio.base.conduit.redstone.signals.CombinedSignal;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * A filter that can be added to a redstone conduit to filter its input
 *
 */
public interface IInputSignalFilter extends IRedstoneSignalFilter {

  @Nonnull
  CombinedSignal apply(@Nonnull CombinedSignal signal, @Nonnull World world, @Nonnull BlockPos pos);

  default boolean shouldUpdate() {
    return false;
  }

}