package crazypants.enderio.machine.reservoir;

import crazypants.enderio.render.MergingBlockStateWrapper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;

public class ReservoirStateWrapper extends MergingBlockStateWrapper {

  public ReservoirStateWrapper(IBlockState state, IBlockAccess world, BlockPos pos) {
    super(state, world, pos);
  }

  @Override
  protected boolean isSameKind(IBlockState other) {
    return getBlock() == other.getBlock();
  }

  @Override
  protected void renderBody() {
  }

  @Override
  protected IBlockState getMergedBlockstate() {
    return null;
  }

  @Override
  protected IBlockState getBorderedBlockstate() {
    return getState();
  }

}
