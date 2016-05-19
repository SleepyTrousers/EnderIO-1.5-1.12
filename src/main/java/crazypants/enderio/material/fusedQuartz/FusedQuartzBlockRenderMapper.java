package crazypants.enderio.material.fusedQuartz;

import java.util.EnumMap;
import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import crazypants.enderio.render.ConnectedBlockRenderMapper;
import crazypants.enderio.render.IBlockStateWrapper;
import crazypants.enderio.render.IOMode.EnumIOMode;
import crazypants.enderio.render.pipeline.QuadCollector;

public class FusedQuartzBlockRenderMapper extends ConnectedBlockRenderMapper {

  public FusedQuartzBlockRenderMapper(IBlockState state, IBlockAccess world, BlockPos pos) {
    super(state, world, pos);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public EnumMap<EnumFacing, EnumIOMode> mapOverlayLayer(IBlockStateWrapper state, IBlockAccess world, BlockPos pos, boolean isPainted) {
    return null;
  }

  @Override
  @SideOnly(Side.CLIENT)
  protected List<IBlockState> renderBody(IBlockStateWrapper state, IBlockAccess world, BlockPos pos, BlockRenderLayer blockLayer, QuadCollector quadCollector) {
    return null;
  }

  @Override
  protected boolean isSameKind(IBlockState state, IBlockState other) {
    return state.getBlock() == other.getBlock() && state.getValue(FusedQuartzType.KIND).connectTo(other.getValue(FusedQuartzType.KIND));
  }

  @Override
  @SideOnly(Side.CLIENT)
  protected IBlockState getMergedBlockstate(IBlockState state) {
    return null;
  }

  @Override
  @SideOnly(Side.CLIENT)
  protected IBlockState getBorderedBlockstate(IBlockState state) {
    return state;
  }

}
