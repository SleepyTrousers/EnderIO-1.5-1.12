package crazypants.enderio.base.filter.redstone;

import javax.annotation.Nonnull;

import crazypants.enderio.base.conduit.redstone.signals.CombinedSignal;
import crazypants.enderio.base.conduit.redstone.signals.Signal;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ComparatorInputSignalFilter implements IInputSignalFilter {

  @Override
  @Nonnull
  public Signal apply(@Nonnull Signal signal, @Nonnull World world, @Nonnull BlockPos pos) {
    IBlockState block = world.getBlockState(pos);
    if (block.hasComparatorInputOverride()) {
      return new Signal(block.getComparatorInputOverride(world, pos), signal.getId());
    }
    return new Signal(CombinedSignal.NONE, signal.getId());
  }

  @Override
  public boolean hasGui() {
    return false;
  }

  @Override
  public boolean shouldUpdate() {
    return true;
  }

}
