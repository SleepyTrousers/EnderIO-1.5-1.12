package crazypants.enderio.machine.solar;

import crazypants.enderio.config.Config;
import crazypants.enderio.render.IBlockStateWrapper;
import crazypants.enderio.render.property.EnumMergingBlockRenderMode;
import crazypants.enderio.render.property.IOMode.EnumIOMode;
import crazypants.enderio.render.rendermapper.ConnectedBlockRenderMapper;
import crazypants.enderio.render.util.QuadCollector;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

import static crazypants.enderio.render.property.EnumMergingBlockRenderMode.RENDER;

public class SolarBlockRenderMapper extends ConnectedBlockRenderMapper {

  public SolarBlockRenderMapper(IBlockState state, IBlockAccess world, BlockPos pos) {
    super(state, world, pos);
    skip_top = true;
    skip_side = true;
    skip_top_side = true;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public EnumMap<EnumFacing, EnumIOMode> mapOverlayLayer(IBlockStateWrapper state, IBlockAccess world, BlockPos pos, boolean isPainted) {
    return null;
  }

  @Override
  @SideOnly(Side.CLIENT)
  protected List<IBlockState> renderBody(IBlockStateWrapper state, IBlockAccess world, BlockPos pos, BlockRenderLayer blockLayer, QuadCollector quadCollector) {
    List<IBlockState> result = new ArrayList<IBlockState>();
    result.add(state.getState().withProperty(RENDER, EnumMergingBlockRenderMode.sides));
    return result;
  }

  @Override
  protected boolean isSameKind(IBlockState state, IBlockAccess world, BlockPos pos, BlockPos other) {
    return pos.getY() == other.getY() && isSameKind(state, world.getBlockState(other));
  }

  @Override
  protected boolean isSameKind(IBlockState state, IBlockState other) {
    return state.getBlock() == other.getBlock()
        && (Config.photovoltaicCanTypesJoins || state.getValue(SolarType.KIND).connectTo(other.getValue(SolarType.KIND)));
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
