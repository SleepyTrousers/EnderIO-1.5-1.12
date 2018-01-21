package crazypants.enderio.machines.machine.solar;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import crazypants.enderio.base.render.IBlockStateWrapper;
import crazypants.enderio.base.render.property.EnumMergingBlockRenderMode;
import crazypants.enderio.base.render.rendermapper.ConnectedBlockRenderMapper;
import crazypants.enderio.base.render.util.QuadCollector;
import crazypants.enderio.machines.config.config.SolarConfig;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static crazypants.enderio.base.render.property.EnumMergingBlockRenderMode.RENDER;

public class SolarBlockRenderMapper extends ConnectedBlockRenderMapper {

  public SolarBlockRenderMapper(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos) {
    super(state, world, pos);
    skip_top = true;
    skip_side = true;
    skip_top_side = true;
  }

  @Override
  @SideOnly(Side.CLIENT)
  protected List<IBlockState> renderBody(@Nonnull IBlockStateWrapper state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, BlockRenderLayer blockLayer,
      @Nonnull QuadCollector quadCollector) {
    List<IBlockState> result = new ArrayList<IBlockState>();
    result.add(state.getState().withProperty(RENDER, EnumMergingBlockRenderMode.sides));
    return result;
  }

  @Override
  protected boolean isSameKind(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nonnull BlockPos other) {
    return pos.getY() == other.getY() && isSameKind(state, world.getBlockState(other));
  }

  @Override
  protected boolean isSameKind(@Nonnull IBlockState state, @Nonnull IBlockState other) {
    return state.getBlock() == other.getBlock()
        && (SolarConfig.canSolarTypesJoin.get() || state.getValue(SolarType.KIND).connectTo(other.getValue(SolarType.KIND)));
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
