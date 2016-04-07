package crazypants.enderio.machine.painter.blocks;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.ITileEntityProvider;
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

public class BlockPaintedStairs extends BlockStairs implements ITileEntityProvider, IPaintable.ITexturePaintableBlock, ISmartRenderAwareBlock,
    IRenderMapper.IBlockRenderMapper.IRenderLayerAware, IRenderMapper.IItemRenderMapper.IItemModelMapper {

  public static BlockPaintedStairs create() {
    BlockPaintedStairs woodStairs = new BlockPaintedStairs(Blocks.oak_stairs, ModObject.blockPaintedStair.getUnlocalisedName());
    woodStairs.init();
    MachineRecipeRegistry.instance.registerRecipe(ModObject.blockPainter.getUnlocalisedName(), new BasicPainterTemplate<BlockPaintedStairs>(woodStairs,
        Blocks.oak_stairs, Blocks.acacia_stairs, Blocks.spruce_stairs, Blocks.birch_stairs, Blocks.jungle_stairs, Blocks.dark_oak_stairs));

    BlockPaintedStairs stoneStairs = new BlockPaintedStairs(Blocks.stone_stairs, ModObject.blockPaintedStoneStair.getUnlocalisedName());
    stoneStairs.init();
    MachineRecipeRegistry.instance.registerRecipe(ModObject.blockPainter.getUnlocalisedName(), new BasicPainterTemplate<BlockPaintedStairs>(stoneStairs,
        Blocks.stone_stairs, Blocks.brick_stairs, Blocks.stone_brick_stairs, Blocks.nether_brick_stairs, Blocks.sandstone_stairs, Blocks.quartz_stairs,
        Blocks.red_sandstone_stairs));

    return woodStairs;
  }

  private final String name;

  public BlockPaintedStairs(Block model, String name) {
    super(model.getDefaultState());
    if (!(model instanceof BlockStairs)) {
      throw new RuntimeException();
    }
    this.setCreativeTab(null);
    this.name = name;
    setUnlocalizedName(name);
  }

  private void init() {
    GameRegistry.registerBlock(this, null, name);
    GameRegistry.registerItem(new BlockItemPaintedBlock(this), name);
    SmartModelAttacher.registerNoProps(this);
    PaintRegistry.registerModel("stairs", new ResourceLocation("minecraft", "block/oak_stairs"), PaintRegistry.PaintMode.ALL_TEXTURES);
    PaintRegistry.registerModel("outer_stairs", new ResourceLocation("minecraft", "block/oak_outer_stairs"), PaintRegistry.PaintMode.ALL_TEXTURES);
    PaintRegistry.registerModel("inner_stairs", new ResourceLocation("minecraft", "block/oak_inner_stairs"), PaintRegistry.PaintMode.ALL_TEXTURES);
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
      blockStateWrapper.addCacheKey(getPaintSource(state, world, pos)).addCacheKey(state.getValue(HALF)).addCacheKey(state.getValue(FACING))
          .addCacheKey(state.getValue(SHAPE));
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

  @SuppressWarnings("deprecation")
  @SideOnly(Side.CLIENT)
  private IBakedModel mapRender(IBlockState state, IBlockState paint) {

    final boolean top = state.getValue(HALF) == EnumHalf.TOP;
    int xRot = top ? 180 : 0;

    int yRot;
    switch (state.getValue(FACING)) {
    case EAST:
      yRot = 0;
      break;
    case NORTH:
      yRot = 270;
      break;
    case SOUTH:
      yRot = 90;
      break;
    case WEST:
      yRot = 180;
      break;
    default:
      return null;
    }

    String model;
    switch (state.getValue(SHAPE)) {
    case INNER_LEFT:
      model = "inner_stairs";
      yRot += top ? +90 : -90;
      break;
    case INNER_RIGHT:
      model = "inner_stairs";
      break;
    case OUTER_LEFT:
      model = "outer_stairs";
      yRot += top ? +90 : -90;
      break;
    case OUTER_RIGHT:
      model = "outer_stairs";
      break;
    case STRAIGHT:
      model = "stairs";
      break;
    default:
      return null;
    }

    return PaintRegistry.getModel(IBakedModel.class, model, paint, new UVLock(ModelRotation.getModelRotation(xRot, yRot)));
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
    if (paintSource != null) {
      IBlockState stdOverlay = BlockMachineBase.block.getDefaultState().withProperty(EnumRenderPart.SUB, EnumRenderPart.PAINT_OVERLAY);
      @SuppressWarnings("deprecation")
      IBakedModel model1 = PaintRegistry.getModel(IBakedModel.class, "stairs", paintSource, new UVLock(null));
      IBakedModel model2 = PaintRegistry.getModel(IBakedModel.class, "stairs", stdOverlay, PaintRegistry.OVERLAY_TRANSFORMATION4);
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
  public int getFlammability(IBlockAccess world, BlockPos pos, EnumFacing face) {
    return getMaterial() == Material.wood ? 20 : super.getFlammability(world, pos, face);
  }

  @Override
  public int getFireSpreadSpeed(IBlockAccess world, BlockPos pos, EnumFacing face) {
    return getMaterial() == Material.wood ? 5 : super.getFireSpreadSpeed(world, pos, face);
  }

  @Override
  public boolean doesSideBlockRendering(IBlockAccess world, BlockPos pos, EnumFacing face) {
    return false;
  }

  @Override
  public boolean shouldSideBeRendered(IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
    if (worldIn.getBlockState(pos).getBlock() instanceof BlockPaintedStairs
        && getPaintSource(null, worldIn, pos) == getPaintSource(null, worldIn, pos.offset(side.getOpposite()))) {
      return false;
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
