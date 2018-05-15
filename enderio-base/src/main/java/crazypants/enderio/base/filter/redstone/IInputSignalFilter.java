package crazypants.enderio.base.filter.redstone;

import javax.annotation.Nonnull;

import crazypants.enderio.base.conduit.redstone.signals.Signal;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * A filter that can be added to a redstone conduit to filter its input
 *
 */
public interface IInputSignalFilter extends IRedstoneSignalFilter {

  @Nonnull
  Signal apply(@Nonnull Signal signal, @Nonnull World world, @Nonnull BlockPos pos);

  default boolean shouldUpdate() {
    return false;
  }

}