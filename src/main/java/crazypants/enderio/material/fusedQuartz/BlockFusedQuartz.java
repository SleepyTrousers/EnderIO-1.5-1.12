package crazypants.enderio.material.fusedQuartz;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import crazypants.enderio.ModObject;
import crazypants.enderio.TileEntityEio;
import crazypants.enderio.render.IBlockStateWrapper;
import crazypants.enderio.render.IRenderMapper.IItemRenderMapper;
import crazypants.enderio.render.ITintedBlock;
import crazypants.enderio.render.pipeline.BlockStateWrapperBase;
import crazypants.enderio.render.property.EnumMergingBlockRenderMode;
import crazypants.enderio.render.registry.SmartModelAttacher;
import crazypants.util.FacadeUtil;
import net.minecraft.block.BlockColored;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static crazypants.enderio.config.Config.glassConnectToTheirColorVariants;

public class BlockFusedQuartz extends BlockFusedQuartzBase<TileEntityEio> implements ITintedBlock {
  
  protected static final EnumDyeColor DEFAULT_COLOR = EnumDyeColor.WHITE;

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
        .withProperty(FusedQuartzType.KIND, FusedQuartzType.FUSED_QUARTZ).withProperty(BlockColored.COLOR, DEFAULT_COLOR));
  }

  protected BlockFusedQuartz(@Nonnull String mo) {
    super(mo, null);
  }

  @Override
  protected void init() {
    super.init();
    SmartModelAttacher.register(this, EnumMergingBlockRenderMode.RENDER, EnumMergingBlockRenderMode.DEFAULTS, EnumMergingBlockRenderMode.AUTO);
  }

  @Override
  protected BlockStateContainer createBlockState() {
    return new BlockStateContainer(this, new IProperty[] { EnumMergingBlockRenderMode.RENDER, FusedQuartzType.KIND, BlockColored.COLOR });
  }

  @Override
  public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
    return state.withProperty(EnumMergingBlockRenderMode.RENDER, EnumMergingBlockRenderMode.AUTO).withProperty(BlockColored.COLOR, DEFAULT_COLOR);
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
    IBlockState otherState = world.getBlockState(pos.offset(side)).getActualState(world, pos.offset(side));
    if (FacadeUtil.instance.isFacaded(otherState)) {
      IBlockState facade = FacadeUtil.instance.getFacade(otherState, world, pos.offset(side), side);
      if (facade != null) {
        otherState = facade;
      }
    }

    if (otherState.getBlock() == this) {
      IBlockState ourState = blockStateIn.getActualState(world, pos);
      return !ourState.getValue(FusedQuartzType.KIND).connectTo(otherState.getValue(FusedQuartzType.KIND))
          || (!glassConnectToTheirColorVariants && ourState.getValue(BlockColored.COLOR) != otherState.getValue(BlockColored.COLOR));
    }
    return true;
  }

  @Override
  public int getBlockTint(IBlockState state, @Nullable IBlockAccess worldIn, @Nullable BlockPos pos, int tintIndex) {
    return state.getValue(BlockColored.COLOR).getMapColor().colorValue;
  }

  @Override
  @Nullable
  public float[] getBeaconColorMultiplier(IBlockState state, World world, BlockPos pos, BlockPos beaconPos) {
    return EntitySheep.getDyeRgb(state.getValue(BlockColored.COLOR));
  }

}
