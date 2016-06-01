package crazypants.enderio.material.fusedQuartz;

import crazypants.enderio.ModObject;
import crazypants.enderio.TileEntityEio;
import crazypants.enderio.render.EnumMergingBlockRenderMode;
import crazypants.enderio.render.IBlockStateWrapper;
import crazypants.enderio.render.IRenderMapper.IItemRenderMapper;
import crazypants.enderio.render.SmartModelAttacher;
import crazypants.enderio.render.pipeline.BlockStateWrapperBase;
import crazypants.util.IFacade;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockFusedQuartz extends BlockFusedQuartzBase<TileEntityEio> {
  
  @SideOnly(Side.CLIENT)
  private static FusedQuartzItemRenderMapper RENDER_MAPPER;

  public static BlockFusedQuartz create() {
    BlockFusedQuartz result = new BlockFusedQuartz();
    result.init();
    return result;
  }

  private BlockFusedQuartz() {
    super(ModObject.blockFusedQuartz.getUnlocalisedName(), null);
    setDefaultState(this.blockState.getBaseState().withProperty(EnumMergingBlockRenderMode.RENDER, EnumMergingBlockRenderMode.AUTO)
        .withProperty(FusedQuartzType.KIND, FusedQuartzType.FUSED_QUARTZ));
  }

  @Override
  protected void init() {
    super.init();
    SmartModelAttacher.register(this, EnumMergingBlockRenderMode.RENDER, EnumMergingBlockRenderMode.DEFAULTS, EnumMergingBlockRenderMode.AUTO);
  }

  @Override
  protected BlockStateContainer createBlockState() {
    return new BlockStateContainer(this, new IProperty[] { EnumMergingBlockRenderMode.RENDER, FusedQuartzType.KIND });
  }

  @Override
  public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
    return state.withProperty(EnumMergingBlockRenderMode.RENDER, EnumMergingBlockRenderMode.AUTO);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
    if (state != null && world != null && pos != null) {
      FusedQuartzBlockRenderMapper renderMapper = new FusedQuartzBlockRenderMapper(state, world, pos);
      IBlockStateWrapper blockStateWrapper = new BlockStateWrapperBase(state, world, pos, renderMapper);
      blockStateWrapper.addCacheKey(state.getValue(FusedQuartzType.KIND));
      blockStateWrapper.addCacheKey(renderMapper);
      blockStateWrapper.bakeModel();
      return blockStateWrapper;
    } else {
      return state;
    }
  }

  @Override
  @SideOnly(Side.CLIENT)
  public IItemRenderMapper getItemRenderMapper() {
    return FusedQuartzItemRenderMapper.instance;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public BlockRenderLayer getBlockLayer() {
    return BlockRenderLayer.SOLID;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public boolean shouldSideBeRendered(IBlockState blockStateIn, IBlockAccess world, BlockPos pos, EnumFacing side) {
    IBlockState otherState = world.getBlockState(pos.offset(side));
    Block otherBlock = otherState.getBlock();
    if (otherBlock == this) {
      IBlockState ourState = world.getBlockState(pos);
      return !ourState.getValue(FusedQuartzType.KIND).connectTo(otherState.getValue(FusedQuartzType.KIND));
    } else if (otherBlock instanceof IFacade) {
      IBlockState facade = ((IFacade) otherBlock).getFacade(world, pos.offset(side), side);
      if (facade != null && facade.getBlock() == this) {
        IBlockState ourState = world.getBlockState(pos);
        return !ourState.getValue(FusedQuartzType.KIND).connectTo(otherState.getValue(FusedQuartzType.KIND));
      }
    }
    return true;
  }

}
