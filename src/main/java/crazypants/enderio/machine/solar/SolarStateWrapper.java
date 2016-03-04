package crazypants.enderio.machine.solar;

import static crazypants.enderio.render.EnumMergingBlockRenderMode.RENDER;

import crazypants.enderio.render.EnumMergingBlockRenderMode;
import crazypants.enderio.render.MergingBlockStateWrapper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;

public class SolarStateWrapper extends MergingBlockStateWrapper {

  public SolarStateWrapper(IBlockState state, IBlockAccess world, BlockPos pos) {
    super(state, world, pos);
  }

  protected void setSkipFlags() {
    skip_top = true;
    skip_side = true;
    skip_top_side = true;
  }

  @Override
  protected boolean isSameKind(BlockPos other) {
    return getPos().getY() == other.getY() && isSameKind(getWorld().getBlockState(other));
  }

  @Override
  protected boolean isSameKind(IBlockState other) {
    return getBlock() == other.getBlock() && getState().getValue(SolarType.KIND).connectTo(other.getValue(SolarType.KIND));
  }

  @Override
  protected void renderBody() {
    states.add(getState().withProperty(RENDER, EnumMergingBlockRenderMode.sides));
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
