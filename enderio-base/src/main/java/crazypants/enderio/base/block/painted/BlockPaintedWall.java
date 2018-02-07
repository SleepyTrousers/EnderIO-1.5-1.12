package crazypants.enderio.base.block.painted;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.base.init.IModObject;
import crazypants.enderio.base.init.ModObject;
import crazypants.enderio.base.paint.IPaintable;
import crazypants.enderio.base.paint.PaintUtil;
import crazypants.enderio.base.paint.render.PaintHelper;
import crazypants.enderio.base.paint.render.PaintRegistry;
import crazypants.enderio.base.paint.render.UVLock;
import crazypants.enderio.base.recipe.MachineRecipeRegistry;
import crazypants.enderio.base.recipe.painter.BasicPainterTemplate;
import crazypants.enderio.base.render.IBlockStateWrapper;
import crazypants.enderio.base.render.ICacheKey;
import crazypants.enderio.base.render.ICustomSubItems;
import crazypants.enderio.base.render.IRenderMapper;
import crazypants.enderio.base.render.ISmartRenderAwareBlock;
import crazypants.enderio.base.render.pipeline.BlockStateWrapperBase;
import crazypants.enderio.base.render.property.EnumRenderPart;
import crazypants.enderio.base.render.registry.SmartModelAttacher;
import crazypants.enderio.base.render.util.QuadCollector;
import crazypants.enderio.util.Prep;
import net.minecraft.block.Block;
import net.minecraft.block.BlockWall;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelRotation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockPaintedWall extends BlockWall implements ITileEntityProvider, IPaintable.ITexturePaintableBlock, ISmartRenderAwareBlock,
    IRenderMapper.IBlockRenderMapper.IRenderLayerAware, IRenderMapper.IItemRenderMapper.IItemModelMapper, IModObject.WithBlockItem, ICustomSubItems {

  public static BlockPaintedWall create(@Nonnull IModObject modObject) {
    BlockPaintedWall result = new BlockPaintedWall(modObject);
    result.init(modObject);
    MachineRecipeRegistry.instance.registerRecipe(MachineRecipeRegistry.PAINTER, new BasicPainterTemplate<BlockPaintedWall>(result, Blocks.COBBLESTONE_WALL));

    return result;
  }

  public BlockPaintedWall(@Nonnull IModObject modObject) {
    super(Blocks.COBBLESTONE);
    Prep.setNoCreativeTab(this);
    modObject.apply(this);
  }

  private void init(@Nonnull IModObject modObject) {
    SmartModelAttacher.registerNoProps(this);
    PaintRegistry.registerModel("wall_post", new ResourceLocation("minecraft", "block/cobblestone_wall_post"), PaintRegistry.PaintMode.ALL_TEXTURES);
    PaintRegistry.registerModel("wall_side", new ResourceLocation("minecraft", "block/cobblestone_wall_side"), PaintRegistry.PaintMode.ALL_TEXTURES);
    PaintRegistry.registerModel("wall_inventory", new ResourceLocation("minecraft", "block/cobblestone_wall_inventory"), PaintRegistry.PaintMode.ALL_TEXTURES);
  }

  @Override
  public Item createBlockItem(@Nonnull IModObject modObject) {
    return modObject.apply(new BlockItemPaintedBlock(this));
  }

  @Override
  public @Nonnull String getLocalizedName() {
    return I18n.translateToLocal(this.getUnlocalizedName() + ".name");
  }

  // @Override
  // public boolean canConnectTo(IBlockAccess worldIn, BlockPos pos) {
  // IBlockState blockState2 = worldIn.getBlockState(pos);
  // Block block = blockState2.getBlock();
  // return super.canConnectTo(worldIn, pos)
  // || block instanceof BlockWall
  // || (block instanceof IPaintable.IBlockPaintableBlock && ((IPaintable.IBlockPaintableBlock) block).getPaintSource(blockState2, worldIn,
  // pos) instanceof BlockWall);
  // }

  @Override
  public boolean canPlaceTorchOnTop(@Nonnull IBlockState bs, @Nonnull IBlockAccess world, @Nonnull BlockPos pos) {
    return true;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public boolean shouldSideBeRendered(@Nonnull IBlockState bs, @Nonnull IBlockAccess worldIn, @Nonnull BlockPos pos, @Nonnull EnumFacing side) {
    if (side.getAxis() != EnumFacing.Axis.Y) {
      // Special case for walls painted with transparent/translucent textures
      final BlockPos otherPos = pos.offset(side);
      IBlockState otherBlockState = worldIn.getBlockState(otherPos);
      if (otherBlockState.getBlock() instanceof BlockPaintedWall && getPaintSource(bs, worldIn, pos) == getPaintSource(otherBlockState, worldIn, otherPos)) {
        return false;
      }
    }
    return super.shouldSideBeRendered(bs, worldIn, pos, side);
  }

  @Override
  public TileEntity createNewTileEntity(@Nonnull World world, int metadata) {
    return new TileEntityPaintedBlock();
  }

  @Override
  public void onBlockPlacedBy(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull EntityLivingBase player,
      @Nonnull ItemStack stack) {
    setPaintSource(state, world, pos, PaintUtil.getSourceBlock(stack));
    if (!world.isRemote) {
      world.notifyBlockUpdate(pos, state, state, 3);
    }
  }

  @Override
  public boolean removedByPlayer(@Nonnull IBlockState bs, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull EntityPlayer player, boolean willHarvest) {
    if (willHarvest) {
      return true;
    }
    return super.removedByPlayer(bs, world, pos, player, willHarvest);
  }

  @Override
  public void harvestBlock(@Nonnull World worldIn, @Nonnull EntityPlayer player, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nullable TileEntity te,
      @Nonnull ItemStack stack) {
    super.harvestBlock(worldIn, player, pos, state, te, stack);
    super.removedByPlayer(state, worldIn, pos, player, true);
  }

  @Override
  public void getDrops(@Nonnull NonNullList<ItemStack> drops, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nonnull IBlockState state, int fortune) {
    NNList<ItemStack> drops2 = new NNList<>();
    super.getDrops(drops2, world, pos, state, fortune);
    for (ItemStack drop : drops2) {
      PaintUtil.setSourceBlock(NullHelper.notnullM(drop, "null stack from getDrops()"), getPaintSource(state, world, pos));
    }
    drops.addAll(drops2);
  }

  @Override
  public @Nonnull ItemStack getPickBlock(@Nonnull IBlockState bs, @Nonnull RayTraceResult target, @Nonnull World world, @Nonnull BlockPos pos,
      @Nonnull EntityPlayer player) {
    final ItemStack pickBlock = super.getPickBlock(bs, target, world, pos, player);
    PaintUtil.setSourceBlock(pickBlock, getPaintSource(bs, world, pos));
    return pickBlock;
  }

  @Override
  public @Nonnull IBlockState getExtendedState(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos) {
    IBlockStateWrapper blockStateWrapper = new BlockStateWrapperBase(state, world, pos, this);
    blockStateWrapper.addCacheKey(getPaintSource(state, world, pos)).addCacheKey(state.getValue(NORTH)).addCacheKey(state.getValue(EAST))
        .addCacheKey(state.getValue(WEST)).addCacheKey(state.getValue(SOUTH)).addCacheKey(state.getValue(UP));
    blockStateWrapper.bakeModel();
    return blockStateWrapper;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public @Nonnull IItemRenderMapper getItemRenderMapper() {
    return this;
  }

  @SideOnly(Side.CLIENT)
  private List<IBakedModel> mapRender(IBlockState state, @Nullable IBlockState paint) {
    List<IBakedModel> result = new ArrayList<IBakedModel>();

    if (state.getValue(UP)) {
      result.add(PaintRegistry.getModel(IBakedModel.class, "wall_post", paint, null));
    }
    if (state.getValue(NORTH)) {
      result.add(PaintRegistry.getModel(IBakedModel.class, "wall_side", paint, new UVLock(null)));
    }
    if (state.getValue(EAST)) {
      result.add(PaintRegistry.getModel(IBakedModel.class, "wall_side", paint, new UVLock(ModelRotation.X0_Y90)));
    }
    if (state.getValue(SOUTH)) {
      result.add(PaintRegistry.getModel(IBakedModel.class, "wall_side", paint, new UVLock(ModelRotation.X0_Y180)));
    }
    if (state.getValue(WEST)) {
      result.add(PaintRegistry.getModel(IBakedModel.class, "wall_side", paint, new UVLock(ModelRotation.X0_Y270)));
    }

    return result;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public @Nonnull ICacheKey getCacheKey(@Nonnull Block block, @Nonnull ItemStack stack, @Nonnull ICacheKey cacheKey) {
    return cacheKey.addCacheKey(getPaintSource(block, stack));
  }

  @Override
  @SideOnly(Side.CLIENT)
  public List<IBakedModel> mapItemRender(@Nonnull Block block, @Nonnull ItemStack stack) {
    IBlockState paintSource = getPaintSource(block, stack);
    IBlockState stdOverlay = ModObject.block_machine_base.getBlockNN().getDefaultState().withProperty(EnumRenderPart.SUB, EnumRenderPart.PAINT_OVERLAY);

    IBakedModel model1 = PaintRegistry.getModel(IBakedModel.class, "wall_inventory", paintSource, new UVLock(null));
    IBakedModel model2 = PaintRegistry.getModel(IBakedModel.class, "wall_inventory", stdOverlay, PaintRegistry.OVERLAY_TRANSFORMATION2);
    List<IBakedModel> list = new ArrayList<IBakedModel>();
    list.add(model1);
    list.add(model2);
    return list;
  }

  @Override
  public boolean canRenderInLayer(@Nonnull IBlockState state, @Nonnull BlockRenderLayer layer) {
    return true;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void getSubBlocks(@Nonnull CreativeTabs tab, @Nonnull NonNullList<ItemStack> list) {
    // Painted blocks don't show in the Creative Inventory or JEI
  }

  @Override
  @Nonnull
  public NNList<ItemStack> getSubItems() {
    return getSubItems(this, 0, BlockWall.EnumType.values().length - 1);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public List<IBlockState> mapBlockRender(@Nonnull IBlockStateWrapper state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, BlockRenderLayer blockLayer,
      @Nonnull QuadCollector quadCollector) {
    IBlockState paintSource = getPaintSource(state, world, pos);
    if (blockLayer == null || PaintUtil.canRenderInLayer(paintSource, blockLayer)) {
      for (IBakedModel model : mapRender(state, paintSource)) {
        quadCollector.addFriendlybakedModel(blockLayer, model, paintSource, MathHelper.getPositionRandom(pos));
      }
    }
    return null;
  }

  @SideOnly(Side.CLIENT)
  @Override
  public boolean addHitEffects(@Nonnull IBlockState state, @Nonnull World world, @Nonnull RayTraceResult target, @Nonnull ParticleManager effectRenderer) {
    return PaintHelper.addHitEffects(state, world, target, effectRenderer);
  }

  @SideOnly(Side.CLIENT)
  @Override
  public boolean addDestroyEffects(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull ParticleManager effectRenderer) {
    return PaintHelper.addDestroyEffects(world, pos, effectRenderer);
  }

}
