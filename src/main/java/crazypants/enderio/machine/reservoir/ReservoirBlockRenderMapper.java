package crazypants.enderio.machine.reservoir;

import java.util.EnumMap;
import java.util.List;

import crazypants.enderio.render.IBlockStateWrapper;
import crazypants.enderio.render.property.IOMode.EnumIOMode;
import crazypants.enderio.render.rendermapper.ConnectedBlockRenderMapper;
import crazypants.enderio.render.util.QuadCollector;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ReservoirBlockRenderMapper extends ConnectedBlockRenderMapper {

  public ReservoirBlockRenderMapper(IBlockState state, IBlockAccess world, BlockPos pos) {
    super(state, world, pos);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public EnumMap<EnumFacing, EnumIOMode> mapOverlayLayer(IBlockStateWrapper state, IBlockAccess world, BlockPos pos, boolean isPainted) {
    TileEntity tileEntity = state.getTileEntity();
    if ((tileEntity instanceof TileReservoir) && ((TileReservoir) tileEntity).isAutoEject()) {
      EnumMap<EnumFacing, EnumIOMode> result = new EnumMap<EnumFacing, EnumIOMode>(EnumFacing.class);
      for (EnumFacing face : EnumFacing.values()) {
        IBlockState neighborState = world.getBlockState(pos.offset(face));
        if (!isSameKind(state, neighborState)) {
          result.put(face, EnumIOMode.RESERVOIR);
        }
      }
      return result.isEmpty() ? null : result;
    }
    return null;
  }

  @Override
  @SideOnly(Side.CLIENT)
  protected List<IBlockState> renderBody(IBlockStateWrapper state, IBlockAccess world, BlockPos pos, BlockRenderLayer blockLayer, QuadCollector quadCollector) {
    return null;
  }

  @Override
  protected boolean isSameKind(IBlockState state, IBlockState other) {
    return state.getBlock() == other.getBlock();
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
