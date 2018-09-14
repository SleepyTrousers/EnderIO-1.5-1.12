package crazypants.enderio.machines.machine.reservoir;

import java.util.EnumMap;
import java.util.List;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NNList.NNIterator;

import crazypants.enderio.base.render.IBlockStateWrapper;
import crazypants.enderio.base.render.property.IOMode.EnumIOMode;
import crazypants.enderio.base.render.rendermapper.ConnectedBlockRenderMapper;
import crazypants.enderio.base.render.util.QuadCollector;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ReservoirBlockRenderMapper extends ConnectedBlockRenderMapper {

  public ReservoirBlockRenderMapper(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos) {
    super(state, world, pos);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public EnumMap<EnumFacing, EnumIOMode> mapOverlayLayer(@Nonnull IBlockStateWrapper state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos,
      boolean isPainted) {
    TileEntity tileEntity = state.getTileEntity();
    if ((tileEntity instanceof TileReservoirBase) && ((TileReservoirBase) tileEntity).isAutoEject()) {
      EnumMap<EnumFacing, EnumIOMode> result = new EnumMap<EnumFacing, EnumIOMode>(EnumFacing.class);
      for (NNIterator<EnumFacing> itr = NNList.FACING.fastIterator(); itr.hasNext();) {
        EnumFacing face = itr.next();
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
  protected List<IBlockState> renderBody(@Nonnull IBlockStateWrapper state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, BlockRenderLayer blockLayer,
      @Nonnull QuadCollector quadCollector) {
    return null;
  }

  @Override
  protected boolean isSameKind(@Nonnull IBlockState state, @Nonnull IBlockState other) {
    return state.getBlock() == other.getBlock();
  }

  @Override
  @SideOnly(Side.CLIENT)
  protected IBlockState getMergedBlockstate(@Nonnull IBlockState state) {
    return null;
  }

  @Override
  @SideOnly(Side.CLIENT)
  protected IBlockState getBorderedBlockstate(@Nonnull IBlockState state) {
    return state;
  }

}
