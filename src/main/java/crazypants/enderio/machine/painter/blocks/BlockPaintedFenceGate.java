package crazypants.enderio.machine.painter.blocks;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.BlockWall;
import net.minecraft.block.ITileEntityProvider;
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
public class BlockPaintedFenceGate extends BlockFenceGate implements ITileEntityProvider, IPaintable.ITexturePaintableBlock, ISmartRenderAwareBlock,
    IRenderMapper.IBlockRenderMapper.IRenderLayerAware, IRenderMapper.IItemRenderMapper.IItemModelMapper {

  public static BlockPaintedFenceGate create() {
    BlockPaintedFenceGate result = new BlockPaintedFenceGate(BlockPlanks.EnumType.OAK, ModObject.blockPaintedFenceGate.getUnlocalisedName());
    result.setHardness(2.0F).setResistance(5.0F).setStepSound(soundTypeWood);
    result.init();
    MachineRecipeRegistry.instance.registerRecipe(ModObject.blockPainter.getUnlocalisedName(), new BasicPainterTemplate<BlockPaintedFenceGate>(result,
        Blocks.oak_fence_gate, Blocks.acacia_fence_gate, Blocks.spruce_fence_gate, Blocks.birch_fence_gate, Blocks.jungle_fence_gate,
        Blocks.dark_oak_fence_gate));

    return result;
  }

  private final String name;

  protected BlockPaintedFenceGate(BlockPlanks.EnumType material, String name) {
    super(material);
    setCreativeTab(null);
    this.name = name;
    setUnlocalizedName(name);
  }

  private void init() {
    GameRegistry.registerBlock(this, null, name);
    GameRegistry.registerItem(new BlockItemPaintedBlock(this), name);
    SmartModelAttacher.registerNoProps(this);
    PaintRegistry.registerModel("fence_gate_closed", new ResourceLocation("minecraft", "block/oak_fence_gate_closed"), PaintRegistry.PaintMode.ALL_TEXTURES);
    PaintRegistry.registerModel("fence_gate_open", new ResourceLocation("minecraft", "block/oak_fence_gate_open"), PaintRegistry.PaintMode.ALL_TEXTURES);
    PaintRegistry.registerModel("wall_gate_closed", new ResourceLocation("minecraft", "block/oak_wall_gate_closed"), PaintRegistry.PaintMode.ALL_TEXTURES);
    PaintRegistry.registerModel("wall_gate_open", new ResourceLocation("minecraft", "block/oak_wall_gate_open"), PaintRegistry.PaintMode.ALL_TEXTURES);
  }

  @Override
  public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
    EnumFacing.Axis enumfacing$axis = state.getValue(FACING).getAxis();

    if (enumfacing$axis == EnumFacing.Axis.Z
        && (worldIn.getBlockState(pos.west()).getBlock() instanceof BlockWall || worldIn.getBlockState(pos.east()).getBlock() instanceof BlockWall)
        || enumfacing$axis == EnumFacing.Axis.X
        && (worldIn.getBlockState(pos.north()).getBlock() instanceof BlockWall || worldIn.getBlockState(pos.south()).getBlock() instanceof BlockWall)) {
      state = state.withProperty(IN_WALL, Boolean.valueOf(true));
    }

    return state;
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
      blockStateWrapper.addCacheKey(getPaintSource(state, world, pos)).addCacheKey(state.getValue(FACING)).addCacheKey(state.getValue(OPEN))
          .addCacheKey(state.getValue(IN_WALL));
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
  private IBakedModel mapRender(IBlockState state, @Nullable IBlockState paint) {
    EnumFacing facing = state.getValue(FACING);
    Boolean open = state.getValue(OPEN);
    Boolean wall = state.getValue(IN_WALL);

    String model;

    if (wall) {
      if (open) {
        model = "wall_gate_open";
      } else {
        model = "wall_gate_closed";
      }
    } else {
      if (open) {
        model = "fence_gate_open";
      } else {
        model = "fence_gate_closed";
      }
    }

    switch (facing) {
    case EAST:
      return PaintRegistry.getModel(IBakedModel.class, model, paint, new UVLock(ModelRotation.X0_Y270));
    case NORTH:
      return PaintRegistry.getModel(IBakedModel.class, model, paint, new UVLock(ModelRotation.X0_Y180));
    case SOUTH:
      return PaintRegistry.getModel(IBakedModel.class, model, paint, new UVLock(null));
    case WEST:
      return PaintRegistry.getModel(IBakedModel.class, model, paint, new UVLock(ModelRotation.X0_Y90));
    default:
      return null;
    }
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
    @SuppressWarnings("deprecation")
    IBakedModel model1 = PaintRegistry.getModel(IBakedModel.class, "fence_gate_closed", paintSource, new UVLock(null));
    IBakedModel model2 = PaintRegistry.getModel(IBakedModel.class, "fence_gate_closed", stdOverlay, PaintRegistry.OVERLAY_TRANSFORMATION2);
    List<IBakedModel> list = new ArrayList<IBakedModel>();
    list.add(model1);
    list.add(model2);
    return list;
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
    return 20;
  }

  @Override
  public int getFireSpreadSpeed(IBlockAccess world, BlockPos pos, EnumFacing face) {
    return 5;
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
    if (PainterUtil2.canRenderInLayer(paintSource, blockLayer)) {
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
