package crazypants.enderio.material.fusedQuartz;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import crazypants.enderio.ModObject;
import crazypants.enderio.TileEntityEio;
import crazypants.enderio.render.EnumMergingBlockRenderMode;
import crazypants.enderio.render.IBlockStateWrapper;
import crazypants.enderio.render.IRenderMapper.IItemRenderMapper;
import crazypants.enderio.render.SmartModelAttacher;
import crazypants.enderio.render.pipeline.BlockStateWrapperBase;

public class BlockFusedQuartz extends BlockFusedQuartzBase<TileEntityEio> {
  
  @SideOnly(Side.CLIENT)
  private static FusedQuartzItemRenderMapper RENDER_MAPPER;

  public static BlockFusedQuartz create() {
    BlockFusedQuartz result = new BlockFusedQuartz();
    result.init();
    return result;
  }

  private BlockFusedQuartz() {
    super(ModObject.blockFusedQuartz.unlocalisedName, null);
    setDefaultState(this.blockState.getBaseState().withProperty(EnumMergingBlockRenderMode.RENDER, EnumMergingBlockRenderMode.AUTO)
        .withProperty(FusedQuartzType.KIND, FusedQuartzType.FUSED_QUARTZ));
  }

  @Override
  protected void init() {
    super.init();
    SmartModelAttacher.register(this, EnumMergingBlockRenderMode.RENDER, EnumMergingBlockRenderMode.DEFAULTS, EnumMergingBlockRenderMode.AUTO);
  }

  @Override
  protected BlockState createBlockState() {
    return new BlockState(this, new IProperty[] { EnumMergingBlockRenderMode.RENDER, FusedQuartzType.KIND });
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
  public IItemRenderMapper getRenderMapper() {
    return FusedQuartzItemRenderMapper.instance;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public EnumWorldBlockLayer getBlockLayer() {
    return EnumWorldBlockLayer.SOLID;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public boolean shouldSideBeRendered(IBlockAccess world, BlockPos pos, EnumFacing side) {
    IBlockState otherState = world.getBlockState(pos);
    Block otherBlock = otherState.getBlock();
    if (otherBlock == this) {
      BlockPos here = pos.offset(side.getOpposite());
      IBlockState ourState = world.getBlockState(here);
      return !ourState.getValue(FusedQuartzType.KIND).connectTo(otherState.getValue(FusedQuartzType.KIND));
    }
    return true;
  }

}
