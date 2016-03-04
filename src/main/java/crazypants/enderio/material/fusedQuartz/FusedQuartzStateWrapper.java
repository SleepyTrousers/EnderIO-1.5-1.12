package crazypants.enderio.material.fusedQuartz;

import crazypants.enderio.render.MergingBlockStateWrapper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;

public class FusedQuartzStateWrapper extends MergingBlockStateWrapper {

  public FusedQuartzStateWrapper(IBlockState state, IBlockAccess world, BlockPos pos) {
    super(state, world, pos);
  }

  @Override
  protected boolean isSameKind(IBlockState other) {
    return getBlock() == other.getBlock() && getState().getValue(FusedQuartzType.KIND).connectTo(other.getValue(FusedQuartzType.KIND));
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
