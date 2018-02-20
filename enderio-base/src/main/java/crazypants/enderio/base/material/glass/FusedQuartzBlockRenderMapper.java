package crazypants.enderio.base.material.glass;

import java.util.List;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;

import crazypants.enderio.base.render.IBlockStateWrapper;
import crazypants.enderio.base.render.property.EnumMergingBlockRenderMode;
import crazypants.enderio.base.render.rendermapper.ConnectedBlockRenderMapper;
import crazypants.enderio.base.render.util.QuadCollector;
import net.minecraft.block.BlockColored;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static crazypants.enderio.base.config.Config.glassConnectToTheirColorVariants;
import static crazypants.enderio.base.render.property.EnumMergingBlockRenderMode.RENDER;

public class FusedQuartzBlockRenderMapper extends ConnectedBlockRenderMapper {

  public FusedQuartzBlockRenderMapper(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos) {
    super(state, world, pos);
  }

  @Override
  @SideOnly(Side.CLIENT)
  protected List<IBlockState> renderBody(@Nonnull IBlockStateWrapper state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, BlockRenderLayer blockLayer,
      @Nonnull QuadCollector quadCollector) {
    return blockLayer == null ? new NNList<>(state.withProperty(RENDER, EnumMergingBlockRenderMode.sides)) : null;
  }

  @Override
  protected boolean isSameKind(@Nonnull IBlockState state, @Nonnull IBlockState other) {
    return other.getBlock() instanceof BlockFusedQuartz && state.getValue(FusedQuartzType.KIND).connectTo(other.getValue(FusedQuartzType.KIND))
        && (glassConnectToTheirColorVariants || (state.getValue(BlockColored.COLOR) == other.getValue(BlockColored.COLOR)));
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
