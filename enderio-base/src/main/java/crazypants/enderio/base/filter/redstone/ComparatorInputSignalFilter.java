package crazypants.enderio.base.filter.redstone;

import javax.annotation.Nonnull;

import crazypants.enderio.base.conduit.redstone.signals.CombinedSignal;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ComparatorInputSignalFilter implements IInputSignalFilter {

  @Override
  @Nonnull
  public CombinedSignal apply(@Nonnull CombinedSignal signal, @Nonnull World world, @Nonnull BlockPos pos) {
    IBlockState block = world.getBlockState(pos);
    if (block.hasComparatorInputOverride()) {
      return new CombinedSignal(block.getComparatorInputOverride(world, pos));
    }
    return CombinedSignal.NONE;
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
