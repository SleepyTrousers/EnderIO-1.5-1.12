package crazypants.enderio.machines.machine.reservoir;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.api.client.gui.IResourceTooltipProvider;
import com.enderio.core.common.util.FluidUtil;

import crazypants.enderio.api.IModObject;
import crazypants.enderio.base.BlockEio;
import crazypants.enderio.base.render.IBlockStateWrapper;
import crazypants.enderio.base.render.IHaveTESR;
import crazypants.enderio.base.render.IRenderMapper.IItemRenderMapper;
import crazypants.enderio.base.render.ISmartRenderAwareBlock;
import crazypants.enderio.base.render.pipeline.BlockStateWrapperBase;
import crazypants.enderio.base.render.property.EnumMergingBlockRenderMode;
import crazypants.enderio.base.render.registry.SmartModelAttacher;
import crazypants.enderio.base.tool.ToolUtil;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class BlockReservoirBase extends BlockEio<TileReservoirBase> implements IResourceTooltipProvider, ISmartRenderAwareBlock, IHaveTESR {

  @SideOnly(Side.CLIENT)
  private static ReservoirItemRenderMapper RENDER_MAPPER;

  public static Block create_omni(@Nonnull IModObject modObject) {
    BlockOmniReservoir res = new BlockOmniReservoir(modObject);
    res.init();
    return res;
  }

  public static Block create(@Nonnull IModObject modObject) {
    BlockReservoir result = new BlockReservoir(modObject);
    result.init();
    return result;
  }

  private static class BlockOmniReservoir extends BlockReservoirBase {

    public BlockOmniReservoir(@Nonnull IModObject modObject) {
      super(modObject);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void bindTileEntitySpecialRenderer() {
      ClientRegistry.bindTileEntitySpecialRenderer(TileReservoirBase.TileOmniReservoir.class, new ReservoirRenderer(this));
    }

    @Override
    protected boolean allowFluidVoiding() {
      return false;
    }

  }

  private static class BlockReservoir extends BlockReservoirBase {

    public BlockReservoir(@Nonnull IModObject modObject) {
      super(modObject);
    }

    @Override
    public @Nullable ItemStack getNBTDrop(@Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nonnull IBlockState state, int fortune,
        @Nullable TileReservoirBase te) {
      return new ItemStack(this, 1, damageDropped(state));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void bindTileEntitySpecialRenderer() {
      ClientRegistry.bindTileEntitySpecialRenderer(TileReservoirBase.class, new ReservoirRenderer(this));
    }

    @Override
    protected boolean allowFluidVoiding() {
      return true;
    }

  }

  protected BlockReservoirBase(@Nonnull IModObject modObject) {
    super(modObject, new Material(MapColor.WATER) {

      @Override
      public boolean isToolNotRequired() {
        return false;
      }

    });
    setSoundType(SoundType.GLASS);
    setDefaultState(getBlockState().getBaseState().withProperty(EnumMergingBlockRenderMode.RENDER, EnumMergingBlockRenderMode.AUTO));
    setShape(mkShape(BlockFaceShape.SOLID));
  }

  @Override
  protected void init() {
    super.init();
    SmartModelAttacher.register(this, EnumMergingBlockRenderMode.RENDER, EnumMergingBlockRenderMode.DEFAULTS, EnumMergingBlockRenderMode.AUTO);
  }

  @Override
  protected @Nonnull BlockStateContainer createBlockState() {
    return new BlockStateContainer(this, new IProperty[] { EnumMergingBlockRenderMode.RENDER });
  }

  @Override
  public @Nonnull IBlockState getStateFromMeta(int meta) {
    return getDefaultState();
  }

  @Override
  public int getMetaFromState(@Nonnull IBlockState state) {
    return 0;
  }

  @Override
  public @Nonnull IBlockState getActualState(@Nonnull IBlockState state, @Nonnull IBlockAccess worldIn, @Nonnull BlockPos pos) {
    return state.withProperty(EnumMergingBlockRenderMode.RENDER, EnumMergingBlockRenderMode.AUTO);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public @Nonnull IBlockState getExtendedState(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos) {
    ReservoirBlockRenderMapper renderMapper = new ReservoirBlockRenderMapper(state, world, pos);
    IBlockStateWrapper blockStateWrapper = new BlockStateWrapperBase(state, world, pos, renderMapper);
    blockStateWrapper.addCacheKey(renderMapper);
    blockStateWrapper.bakeModel();
    return blockStateWrapper;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public @Nonnull IItemRenderMapper getItemRenderMapper() {
    return ReservoirItemRenderMapper.instance;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public @Nonnull BlockRenderLayer getBlockLayer() {
    return BlockRenderLayer.SOLID;
  }

  @Override
  public boolean canRenderInLayer(@Nonnull IBlockState state, @Nonnull BlockRenderLayer layer) {
    return true;
  }

  @Override
  public boolean onBlockActivated(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull EntityPlayer entityPlayer,
      @Nonnull EnumHand hand, @Nonnull EnumFacing side, float hitX, float hitY, float hitZ) {
    if (!entityPlayer.isSneaking()) {
      TileReservoirBase tank = getTileEntity(world, pos);
      if (tank != null) {
        if (ToolUtil.isToolEquipped(entityPlayer, hand)) {
          tank.setAutoEject(!tank.isAutoEject());
          world.notifyBlockUpdate(pos, state, state, 3);
          return true;
        }
        if (tank.getTank().getAvailableSpace() >= Fluid.BUCKET_VOLUME && FluidUtil.fillInternalTankFromPlayerHandItem(world, pos, entityPlayer, hand, tank)) {
          return true;
        }
        if (!tank.getTank().isFull()
            && FluidUtil.fillInternalTankFromPlayerHandItem(world, pos, entityPlayer, hand, new ReservoirTankWrapper(tank, world, pos, allowFluidVoiding()))) {
          return true;
        }
        if (FluidUtil.fillPlayerHandItemFromInternalTank(world, pos, entityPlayer, hand, tank)) {
          return true;
        }
      }
    } else if (ToolUtil.breakBlockWithTool(this, world, pos, side, entityPlayer, hand, permissionNodeWrenching)) {
      return true;
    }
    return false; // super also has fluid transfer code, avoid that
  }

  protected abstract boolean allowFluidVoiding();

  @Override
  public boolean isOpaqueCube(@Nonnull IBlockState bs) {
    return false;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public boolean shouldSideBeRendered(@Nonnull IBlockState bs, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nonnull EnumFacing side) {
    return !(world.getBlockState(pos.offset(side)).getBlock() == this)
        && !world.getBlockState(pos.offset(side)).doesSideBlockRendering(world, pos.offset(side), side.getOpposite());
  }

  @Override
  public @Nonnull String getUnlocalizedNameForTooltip(@Nonnull ItemStack stack) {
    return getUnlocalizedName();
  }

}
