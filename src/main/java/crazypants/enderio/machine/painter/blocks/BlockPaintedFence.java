package crazypants.enderio.machine.painter.blocks;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.BlockEnder;

import crazypants.enderio.ModObject;
import crazypants.enderio.machine.MachineRecipeRegistry;
import crazypants.enderio.machine.painter.recipe.BasicPainterTemplate;
import crazypants.enderio.paint.IPaintable;
import crazypants.enderio.paint.PainterUtil2;
import crazypants.enderio.paint.render.PaintHelper;
import crazypants.enderio.paint.render.PaintRegistry;
import crazypants.enderio.paint.render.UVLock;
import crazypants.enderio.render.IBlockStateWrapper;
import crazypants.enderio.render.ICacheKey;
import crazypants.enderio.render.IRenderMapper;
import crazypants.enderio.render.ISmartRenderAwareBlock;
import crazypants.enderio.render.dummy.BlockMachineBase;
import crazypants.enderio.render.pipeline.BlockStateWrapperBase;
import crazypants.enderio.render.property.EnumRenderPart;
import crazypants.enderio.render.property.IOMode.EnumIOMode;
import crazypants.enderio.render.registry.SmartModelAttacher;
import crazypants.enderio.render.util.QuadCollector;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
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
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockPaintedFence extends BlockFence implements ITileEntityProvider, IPaintable.ITexturePaintableBlock, ISmartRenderAwareBlock,
    IRenderMapper.IBlockRenderMapper.IRenderLayerAware, IRenderMapper.IItemRenderMapper.IItemModelMapper {

  public static BlockPaintedFence create() {
    BlockPaintedFence woodFence = new BlockPaintedFence(Material.WOOD, BlockPlanks.EnumType.OAK.getMapColor(), ModObject.blockPaintedFence.getUnlocalisedName(), SoundType.WOOD);
    woodFence.setHardness(2.0F).setResistance(5.0F);
    woodFence.init();
    MachineRecipeRegistry.instance.registerRecipe(ModObject.blockPainter.getUnlocalisedName(), new BasicPainterTemplate<BlockPaintedFence>(woodFence,
        Blocks.OAK_FENCE, Blocks.ACACIA_FENCE, Blocks.SPRUCE_FENCE, Blocks.BIRCH_FENCE, Blocks.JUNGLE_FENCE, Blocks.DARK_OAK_FENCE));

    return woodFence;
  }

  public static BlockPaintedFence create_stone() {
    BlockPaintedFence stoneFence = new BlockPaintedFence(Material.ROCK, MapColor.NETHERRACK, ModObject.blockPaintedStoneFence.getUnlocalisedName(), SoundType.STONE);
    stoneFence.setHardness(2.0F).setResistance(10.0F);
    stoneFence.init();
    MachineRecipeRegistry.instance.registerRecipe(ModObject.blockPainter.getUnlocalisedName(), new BasicPainterTemplate<BlockPaintedFence>(stoneFence,
        Blocks.NETHER_BRICK_FENCE));

    return stoneFence;
  }

  private final String name;

  protected BlockPaintedFence(Material material, MapColor mapColor, String name, SoundType sound) {
    super(material, mapColor);
    setCreativeTab(null);
    this.name = name;
    setUnlocalizedName(name);
    setRegistryName(name);
    setSoundType(sound);
  }

  private void init() {
    GameRegistry.register(this);
    GameRegistry.register(new BlockItemPaintedBlock(this, name));
    SmartModelAttacher.registerNoProps(this);
    PaintRegistry.registerModel("fence_post", new ResourceLocation("minecraft", "block/oak_fence_post"), PaintRegistry.PaintMode.ALL_TEXTURES);
    PaintRegistry.registerModel("fence_side", new ResourceLocation("minecraft", "block/oak_fence_side"), PaintRegistry.PaintMode.ALL_TEXTURES);
    PaintRegistry.registerModel("fence_inventory", new ResourceLocation("minecraft", "block/oak_fence_inventory"), PaintRegistry.PaintMode.ALL_TEXTURES);
  }

  @Override
  public boolean canConnectTo(IBlockAccess worldIn, BlockPos pos) {
    if (super.canConnectTo(worldIn, pos)) {
      return true;
    }
    final IBlockState blockState2 = worldIn.getBlockState(pos);
    final Block block = blockState2.getBlock();
    if (block instanceof BlockPaintedFence) {
      return true;
    }
    if (block instanceof IPaintable.IBlockPaintableBlock) {
      final IBlockState paintSource = ((IPaintable.IBlockPaintableBlock) block).getPaintSource(blockState2, worldIn, pos);
      return paintSource != null && paintSource.getBlock() instanceof BlockFence && paintSource.getMaterial() == blockMaterial;
    }
    return false;
  }

  @Override
  public TileEntity createNewTileEntity(World world, int metadata) {
    return new TileEntityPaintedBlock();
  }

  @Override
  public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase player, ItemStack stack) {
    setPaintSource(state, world, pos, PainterUtil2.getSourceBlock(stack));
    if (!world.isRemote) {
      world.notifyBlockUpdate(pos, state, state, 3);
    }
  }

  @Override
  public boolean removedByPlayer(IBlockState bs, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
    if (willHarvest) {
      return true;
    }
    return super.removedByPlayer(bs, world, pos, player, willHarvest);
  }

  @Override
  public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, @Nullable ItemStack stack) {
    super.harvestBlock(worldIn, player, pos, state, te, stack);
    super.removedByPlayer(state, worldIn, pos, player, true);
  }

  @Override
  public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
    List<ItemStack> drops = super.getDrops(world, pos, state, fortune);
    for (ItemStack drop : drops) {
      PainterUtil2.setSourceBlock(drop, getPaintSource(state, world, pos));
      }
    return drops;
  }

  @Override
  public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
    final ItemStack pickBlock = super.getPickBlock(state, target, world, pos, player);
    PainterUtil2.setSourceBlock(pickBlock, getPaintSource(null, world, pos));
    return pickBlock;
  }

  @Override
  public void setPaintSource(IBlockState state, IBlockAccess world, BlockPos pos, @Nullable IBlockState paintSource) {
    TileEntity te = world.getTileEntity(pos);
    if (te instanceof IPaintable.IPaintableTileEntity) {
      ((IPaintableTileEntity) te).setPaintSource(paintSource);
    }
  }

  @Override
  public void setPaintSource(Block block, ItemStack stack, @Nullable IBlockState paintSource) {
    PainterUtil2.setSourceBlock(stack, paintSource);
  }

  @Override
  public IBlockState getPaintSource(IBlockState state, IBlockAccess world, BlockPos pos) {
    TileEntity te = BlockEnder.getAnyTileEntitySafe(world, pos);
    if (te instanceof IPaintable.IPaintableTileEntity) {
      return ((IPaintableTileEntity) te).getPaintSource();
    }
    return null;
  }

  @Override
  public IBlockState getPaintSource(Block block, ItemStack stack) {
    return PainterUtil2.getSourceBlock(stack);
  }

  @Override
  public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
    if (state != null && world != null && pos != null) {
      IBlockStateWrapper blockStateWrapper = new BlockStateWrapperBase(state, world, pos, this);
      blockStateWrapper.addCacheKey(getPaintSource(state, world, pos)).addCacheKey(state.getValue(BlockFence.EAST))
          .addCacheKey(state.getValue(BlockFence.NORTH)).addCacheKey(state.getValue(BlockFence.SOUTH)).addCacheKey(state.getValue(BlockFence.WEST));
      blockStateWrapper.bakeModel();
      return blockStateWrapper;
    } else {
      return state;
    }
  }

  @Override
  @SideOnly(Side.CLIENT)
  public IItemRenderMapper getItemRenderMapper() {
    return this;
  }

  @SideOnly(Side.CLIENT)
  private List<IBakedModel> mapRender(IBlockState state, @Nullable IBlockState paint) {
    List<IBakedModel> result = new ArrayList<IBakedModel>();

    result.add(PaintRegistry.getModel(IBakedModel.class, "fence_post", paint, null));

    if (state.getValue(BlockFence.NORTH)) {
      result.add(PaintRegistry.getModel(IBakedModel.class, "fence_side", paint, new UVLock(null)));
    }
    if (state.getValue(BlockFence.EAST)) {
      result.add(PaintRegistry.getModel(IBakedModel.class, "fence_side", paint, new UVLock(ModelRotation.X0_Y90)));
    }
    if (state.getValue(BlockFence.SOUTH)) {
      result.add(PaintRegistry.getModel(IBakedModel.class, "fence_side", paint, new UVLock(ModelRotation.X0_Y180)));
    }
    if (state.getValue(BlockFence.WEST)) {
      result.add(PaintRegistry.getModel(IBakedModel.class, "fence_side", paint, new UVLock(ModelRotation.X0_Y270)));
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
  public List<IBakedModel> mapItemRender(Block block, ItemStack stack) {
    IBlockState paintSource = getPaintSource(block, stack);
    IBlockState stdOverlay = BlockMachineBase.block.getDefaultState().withProperty(EnumRenderPart.SUB, EnumRenderPart.PAINT_OVERLAY);
    IBakedModel model1 = PaintRegistry.getModel(IBakedModel.class, "fence_inventory", paintSource, new UVLock(null));
    IBakedModel model2 = PaintRegistry.getModel(IBakedModel.class, "fence_inventory", stdOverlay, PaintRegistry.OVERLAY_TRANSFORMATION2);
    List<IBakedModel> list = new ArrayList<IBakedModel>();
    list.add(model1);
    list.add(model2);
    return list;
  }

  @Override
  public boolean canRenderInLayer(BlockRenderLayer layer) {
    return true;
  }

  @Override
  public int getFlammability(IBlockAccess world, BlockPos pos, EnumFacing face) {
    IBlockState bs = world.getBlockState(pos);
    return bs.getMaterial() == Material.WOOD ? 20 : super.getFlammability(world, pos, face);
  }

  @Override
  public int getFireSpreadSpeed(IBlockAccess world, BlockPos pos, EnumFacing face) {
    IBlockState bs = world.getBlockState(pos);
    return bs.getMaterial() == Material.WOOD ? 5 : super.getFireSpreadSpeed(world, pos, face);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public boolean shouldSideBeRendered(IBlockState state, IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
    if (side.getAxis() != EnumFacing.Axis.Y) {
      IBlockState blockState2 = worldIn.getBlockState(pos);
      if (blockState2.getBlock() instanceof BlockPaintedFence
          && getPaintSource(blockState2, worldIn, pos) == getPaintSource(blockState2, worldIn, pos.offset(side.getOpposite()))) {
        return false;
      }
    }
    return super.shouldSideBeRendered(state, worldIn, pos, side);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
    if (tab != null) {
      super.getSubBlocks(itemIn, tab, list);
    }
  }

  @Override
  @SideOnly(Side.CLIENT)
  public List<IBlockState> mapBlockRender(IBlockStateWrapper state, IBlockAccess world, BlockPos pos, BlockRenderLayer blockLayer,
      QuadCollector quadCollector) {
    IBlockState paintSource = getPaintSource(state, world, pos);
    if (PainterUtil2.canRenderInLayer(paintSource, blockLayer)) {
      for (IBakedModel model : mapRender(state, paintSource)) {
        quadCollector.addFriendlybakedModel(blockLayer, model, paintSource, MathHelper.getPositionRandom(pos));
      }
    }
    return null;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public EnumMap<EnumFacing, EnumIOMode> mapOverlayLayer(IBlockStateWrapper state, IBlockAccess world, BlockPos pos, boolean isPainted) {
    return null;
  }

  @SideOnly(Side.CLIENT)
  @Override
  public boolean addHitEffects(IBlockState state, World world, RayTraceResult target, ParticleManager effectRenderer) {
    return PaintHelper.addHitEffects(state, world, target, effectRenderer);
  }

  @SideOnly(Side.CLIENT)
  @Override
  public boolean addDestroyEffects(World world, BlockPos pos, ParticleManager effectRenderer) {
    return PaintHelper.addDestroyEffects(world, pos, effectRenderer);
  }

}
