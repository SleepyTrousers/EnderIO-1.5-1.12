package crazypants.enderio.machine.painter.blocks;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelRotation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader.UVLock;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.MachineRecipeRegistry;
import crazypants.enderio.machine.painter.recipe.BasicPainterTemplate;
import crazypants.enderio.paint.IPaintable;
import crazypants.enderio.paint.PainterUtil2;
import crazypants.enderio.paint.render.PaintRegistry;
import crazypants.enderio.render.EnumRenderPart;
import crazypants.enderio.render.IBlockStateWrapper;
import crazypants.enderio.render.ICacheKey;
import crazypants.enderio.render.IOMode.EnumIOMode;
import crazypants.enderio.render.IRenderMapper;
import crazypants.enderio.render.ISmartRenderAwareBlock;
import crazypants.enderio.render.SmartModelAttacher;
import crazypants.enderio.render.dummy.BlockMachineBase;
import crazypants.enderio.render.pipeline.BlockStateWrapperBase;
import crazypants.enderio.render.pipeline.QuadCollector;

@SuppressWarnings("deprecation")
public class BlockPaintedFence extends BlockFence implements ITileEntityProvider, IPaintable.ITexturePaintableBlock, ISmartRenderAwareBlock,
    IRenderMapper.IBlockRenderMapper.IRenderLayerAware, IRenderMapper.IItemRenderMapper.IItemModelMapper {

  public static BlockPaintedFence create() {
    BlockPaintedFence woodFence = new BlockPaintedFence(Material.wood, BlockPlanks.EnumType.OAK.getMapColor(), ModObject.blockPaintedFence.unlocalisedName);
    woodFence.setHardness(2.0F).setResistance(5.0F).setStepSound(soundTypeWood);
    woodFence.init();
    MachineRecipeRegistry.instance.registerRecipe(ModObject.blockPainter.unlocalisedName, new BasicPainterTemplate<BlockPaintedFence>(woodFence,
        Blocks.oak_fence, Blocks.acacia_fence, Blocks.spruce_fence, Blocks.birch_fence, Blocks.jungle_fence, Blocks.dark_oak_fence));

    BlockPaintedFence stoneFence = new BlockPaintedFence(Material.rock, MapColor.netherrackColor, ModObject.blockPaintedStoneFence.unlocalisedName);
    stoneFence.setHardness(2.0F).setResistance(10.0F).setStepSound(soundTypePiston);
    stoneFence.init();
    MachineRecipeRegistry.instance.registerRecipe(ModObject.blockPainter.unlocalisedName, new BasicPainterTemplate<BlockPaintedFence>(stoneFence,
        Blocks.nether_brick_fence));

    return woodFence;
  }

  private final String name;

  protected BlockPaintedFence(Material material, MapColor mapColor, String name) {
    super(material, mapColor);
    setCreativeTab(null);
    this.name = name;
    setUnlocalizedName(name);
  }

  private void init() {
    GameRegistry.registerBlock(this, null, name);
    GameRegistry.registerItem(new BlockItemPaintedBlock(this), name);
    SmartModelAttacher.registerNoProps(this);
    PaintRegistry.registerModel("fence_post", new ResourceLocation("minecraft", "block/oak_fence_post"), PaintRegistry.PaintMode.ALL_TEXTURES);
    PaintRegistry.registerModel("fence_n", new ResourceLocation("minecraft", "block/oak_fence_n"), PaintRegistry.PaintMode.ALL_TEXTURES);
    PaintRegistry.registerModel("fence_ne", new ResourceLocation("minecraft", "block/oak_fence_ne"), PaintRegistry.PaintMode.ALL_TEXTURES);
    PaintRegistry.registerModel("fence_ns", new ResourceLocation("minecraft", "block/oak_fence_ns"), PaintRegistry.PaintMode.ALL_TEXTURES);
    PaintRegistry.registerModel("fence_nse", new ResourceLocation("minecraft", "block/oak_fence_nse"), PaintRegistry.PaintMode.ALL_TEXTURES);
    PaintRegistry.registerModel("fence_nsew", new ResourceLocation("minecraft", "block/oak_fence_nsew"), PaintRegistry.PaintMode.ALL_TEXTURES);
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
      return paintSource != null && paintSource.getBlock() instanceof BlockFence && paintSource.getBlock().getMaterial() == blockMaterial;
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
      world.markBlockForUpdate(pos);
    }
  }

  @Override
  public boolean removedByPlayer(World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
    if (willHarvest) {
      return true;
    }
    return super.removedByPlayer(world, pos, player, willHarvest);
  }

  @Override
  public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity te) {
    super.harvestBlock(worldIn, player, pos, state, te);
    super.removedByPlayer(worldIn, pos, player, true);
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
  public ItemStack getPickBlock(MovingObjectPosition target, World world, BlockPos pos, EntityPlayer player) {
    final ItemStack pickBlock = super.getPickBlock(target, world, pos, player);
    PainterUtil2.setSourceBlock(pickBlock, getPaintSource(null, world, pos));
    return pickBlock;
  }

  @Override
  public void setPaintSource(IBlockState state, IBlockAccess world, BlockPos pos, IBlockState paintSource) {
    TileEntity te = world.getTileEntity(pos);
    if (te instanceof IPaintable.IPaintableTileEntity) {
      ((IPaintableTileEntity) te).setPaintSource(paintSource);
    }
  }

  @Override
  public void setPaintSource(Block block, ItemStack stack, IBlockState paintSource) {
    PainterUtil2.setSourceBlock(stack, paintSource);
  }

  @Override
  public IBlockState getPaintSource(IBlockState state, IBlockAccess world, BlockPos pos) {
    TileEntity te = world.getTileEntity(pos);
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
  public IItemRenderMapper getRenderMapper() {
    return this;
  }

  @SuppressWarnings("deprecation")
  @SideOnly(Side.CLIENT)
  private IBakedModel mapRender(IBlockState state, IBlockState paint) {
    int x = (state.getValue(BlockFence.EAST) ? 8 : 0) + (state.getValue(BlockFence.NORTH) ? 4 : 0) + (state.getValue(BlockFence.SOUTH) ? 2 : 0)
        + (state.getValue(BlockFence.WEST) ? 1 : 0);
    switch (x) {
    case 0 + 0 + 0 + 0:
      return PaintRegistry.getModel(IBakedModel.class, "fence_post", paint, new UVLock(null));
    case 0 + 0 + 0 + 1:
      return PaintRegistry.getModel(IBakedModel.class, "fence_n", paint, new UVLock(ModelRotation.X0_Y270));
    case 0 + 0 + 2 + 0:
      return PaintRegistry.getModel(IBakedModel.class, "fence_n", paint, new UVLock(ModelRotation.X0_Y180));
    case 0 + 0 + 2 + 1:
      return PaintRegistry.getModel(IBakedModel.class, "fence_ne", paint, new UVLock(ModelRotation.X0_Y180));
    case 0 + 4 + 0 + 0:
      return PaintRegistry.getModel(IBakedModel.class, "fence_n", paint, new UVLock(null));
    case 0 + 4 + 0 + 1:
      return PaintRegistry.getModel(IBakedModel.class, "fence_ne", paint, new UVLock(ModelRotation.X0_Y270));
    case 0 + 4 + 2 + 0:
      return PaintRegistry.getModel(IBakedModel.class, "fence_ns", paint, new UVLock(null));
    case 0 + 4 + 2 + 1:
      return PaintRegistry.getModel(IBakedModel.class, "fence_nse", paint, new UVLock(ModelRotation.X0_Y180));
    case 8 + 0 + 0 + 0:
      return PaintRegistry.getModel(IBakedModel.class, "fence_n", paint, new UVLock(ModelRotation.X0_Y90));
    case 8 + 0 + 0 + 1:
      return PaintRegistry.getModel(IBakedModel.class, "fence_ns", paint, new UVLock(ModelRotation.X0_Y90));
    case 8 + 0 + 2 + 0:
      return PaintRegistry.getModel(IBakedModel.class, "fence_ne", paint, new UVLock(ModelRotation.X0_Y90));
    case 8 + 0 + 2 + 1:
      return PaintRegistry.getModel(IBakedModel.class, "fence_nse", paint, new UVLock(ModelRotation.X0_Y90));
    case 8 + 4 + 0 + 0:
      return PaintRegistry.getModel(IBakedModel.class, "fence_ne", paint, new UVLock(null));
    case 8 + 4 + 0 + 1:
      return PaintRegistry.getModel(IBakedModel.class, "fence_nse", paint, new UVLock(ModelRotation.X0_Y270));
    case 8 + 4 + 2 + 0:
      return PaintRegistry.getModel(IBakedModel.class, "fence_nse", paint, new UVLock(null));
    case 8 + 4 + 2 + 1:
      return PaintRegistry.getModel(IBakedModel.class, "fence_nsew", paint, new UVLock(null));
    }
    return null;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public ICacheKey getCacheKey(Block block, ItemStack stack, ICacheKey cacheKey) {
    return cacheKey.addCacheKey(getPaintSource(block, stack));
  }

  @Override
  @SideOnly(Side.CLIENT)
  public List<IBakedModel> mapItemRender(Block block, ItemStack stack) {
    IBlockState paintSource = getPaintSource(block, stack);
    if (paintSource != null) {
      IBlockState stdOverlay = BlockMachineBase.block.getDefaultState().withProperty(EnumRenderPart.SUB, EnumRenderPart.PAINT_OVERLAY);
      @SuppressWarnings("deprecation")
      IBakedModel model1 = PaintRegistry.getModel(IBakedModel.class, "fence_inventory", paintSource, new UVLock(null));
      IBakedModel model2 = PaintRegistry.getModel(IBakedModel.class, "fence_inventory", stdOverlay, PaintRegistry.OVERLAY_TRANSFORMATION2);
      List<IBakedModel> list = new ArrayList<IBakedModel>();
      list.add(model1);
      list.add(model2);
      return list;
    } else {
      return null;
    }
  }

  @Override
  public boolean canRenderInLayer(EnumWorldBlockLayer layer) {
    return true;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public int colorMultiplier(IBlockAccess worldIn, BlockPos pos, int renderPass) {
    IBlockState paintSource = getPaintSource(null, worldIn, pos);
    if (paintSource != null) {
      try {
        return paintSource.getBlock().colorMultiplier(worldIn, pos, renderPass);
      } catch (Throwable e) {
      }
    }
    return super.colorMultiplier(worldIn, pos, renderPass);
  }

  @Override
  public int getFlammability(IBlockAccess world, BlockPos pos, EnumFacing face) {
    return getMaterial() == Material.wood ? 20 : super.getFlammability(world, pos, face);
  }

  @Override
  public int getFireSpreadSpeed(IBlockAccess world, BlockPos pos, EnumFacing face) {
    return getMaterial() == Material.wood ? 5 : super.getFireSpreadSpeed(world, pos, face);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public boolean shouldSideBeRendered(IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
    if (side.getAxis() != EnumFacing.Axis.Y) {
      IBlockState blockState2 = worldIn.getBlockState(pos);
      if (blockState2.getBlock() instanceof BlockPaintedFence
          && getPaintSource(blockState2, worldIn, pos) == getPaintSource(blockState2, worldIn, pos.offset(side.getOpposite()))) {
        return false;
      }
    }
    return super.shouldSideBeRendered(worldIn, pos, side);
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
  public List<IBlockState> mapBlockRender(IBlockStateWrapper state, IBlockAccess world, BlockPos pos, EnumWorldBlockLayer blockLayer,
      QuadCollector quadCollector) {
    IBlockState paintSource = getPaintSource(state, world, pos);
    if (paintSource != null && paintSource.getBlock().canRenderInLayer(blockLayer)) {
      quadCollector.addFriendlybakedModel(blockLayer, mapRender(state, paintSource), paintSource, MathHelper.getPositionRandom(pos));
    }
    return null;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public EnumMap<EnumFacing, EnumIOMode> mapOverlayLayer(IBlockStateWrapper state, IBlockAccess world, BlockPos pos, boolean isPainted) {
    return null;
  }

}
