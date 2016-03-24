package crazypants.enderio.material.fusedQuartz;

import java.util.EnumMap;
import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.IBlockAccess;
import crazypants.enderio.render.ConnectedBlockRenderMapper;
import crazypants.enderio.render.IBlockStateWrapper;
import crazypants.enderio.render.IOMode.EnumIOMode;
import crazypants.enderio.render.pipeline.QuadCollector;

public class FusedQuartzBlockRenderMapper extends ConnectedBlockRenderMapper {

  public FusedQuartzBlockRenderMapper(IBlockState state, IBlockAccess world, BlockPos pos) {
    super(state, world, pos);
  }

  @Override
  public EnumMap<EnumFacing, EnumIOMode> mapOverlayLayer(IBlockStateWrapper state, IBlockAccess world, BlockPos pos, boolean isPainted) {
    return null;
  }

  @Override
  protected List<IBlockState> renderBody(IBlockStateWrapper state, IBlockAccess world, BlockPos pos, EnumWorldBlockLayer blockLayer, QuadCollector quadCollector) {
    return null;
  }

  @Override
  protected boolean isSameKind(IBlockState state, IBlockState other) {
    return state.getBlock() == other.getBlock() && state.getValue(FusedQuartzType.KIND).connectTo(other.getValue(FusedQuartzType.KIND));
  }

  @Override
  protected IBlockState getMergedBlockstate(IBlockState state) {
    return null;
  }

  @Override
  protected IBlockState getBorderedBlockstate(IBlockState state) {
    return state;
  }

}
