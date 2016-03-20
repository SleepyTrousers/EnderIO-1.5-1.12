package crazypants.enderio.machine.painter.blocks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.block.Block;
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
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.model.ModelLoader.UVLock;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.apache.commons.lang3.tuple.Pair;

import crazypants.enderio.ModObject;
import crazypants.enderio.machine.MachineRecipeRegistry;
import crazypants.enderio.machine.painter.recipe.BasicPainterTemplate;
import crazypants.enderio.paint.IPaintable;
import crazypants.enderio.paint.PainterUtil2;
import crazypants.enderio.paint.render.PaintRegistry;
import crazypants.enderio.render.BlockStateWrapper;
import crazypants.enderio.render.EnumRenderPart;
import crazypants.enderio.render.IRenderMapper;
import crazypants.enderio.render.ISmartRenderAwareBlock;
import crazypants.enderio.render.SmartModelAttacher;
import crazypants.enderio.render.dummy.BlockMachineBase;

public class BlockPaintedWall extends BlockWall implements ITileEntityProvider, IPaintable.ITexturePaintableBlock, ISmartRenderAwareBlock,
    IRenderMapper.IRenderLayerAware {

  public static BlockPaintedWall create() {
    BlockPaintedWall result = new BlockPaintedWall(ModObject.blockPaintedWall.unlocalisedName);
    result.init();
    MachineRecipeRegistry.instance.registerRecipe(ModObject.blockPainter.unlocalisedName, new BasicPainterTemplate<BlockPaintedWall>(result,
        Blocks.cobblestone_wall));

    return result;
  }

  private final String name;

  public BlockPaintedWall(String name) {
    super(Blocks.cobblestone);
    setCreativeTab(null);
    this.name = name;
    setUnlocalizedName(name);
  }

  private void init() {
    GameRegistry.registerBlock(this, null, name);
    GameRegistry.registerItem(new BlockItemPaintedBlock(this), name);
    SmartModelAttacher.registerNoProps(this);
    PaintRegistry.registerModel("wall_post", new ResourceLocation("minecraft", "block/cobblestone_wall_post"), PaintRegistry.PaintMode.ALL_TEXTURES);
    PaintRegistry.registerModel("wall_n", new ResourceLocation("minecraft", "block/cobblestone_wall_n"), PaintRegistry.PaintMode.ALL_TEXTURES);
    PaintRegistry.registerModel("wall_ne", new ResourceLocation("minecraft", "block/cobblestone_wall_ne"), PaintRegistry.PaintMode.ALL_TEXTURES);
    PaintRegistry.registerModel("wall_ns", new ResourceLocation("minecraft", "block/cobblestone_wall_ns"), PaintRegistry.PaintMode.ALL_TEXTURES);
    PaintRegistry.registerModel("wall_nse", new ResourceLocation("minecraft", "block/cobblestone_wall_nse"), PaintRegistry.PaintMode.ALL_TEXTURES);
    PaintRegistry.registerModel("wall_nsew", new ResourceLocation("minecraft", "block/cobblestone_wall_nsew"), PaintRegistry.PaintMode.ALL_TEXTURES);
    PaintRegistry.registerModel("wall_ns_above", new ResourceLocation("minecraft", "block/cobblestone_wall_ns_above"), PaintRegistry.PaintMode.ALL_TEXTURES);
    PaintRegistry.registerModel("wall_inventory", new ResourceLocation("minecraft", "block/cobblestone_wall_inventory"), PaintRegistry.PaintMode.ALL_TEXTURES);
  }

  @Override
  public String getLocalizedName() {
    return StatCollector.translateToLocal(this.getUnlocalizedName() + ".name");
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
  public boolean canPlaceTorchOnTop(IBlockAccess world, BlockPos pos) {
    return true;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public boolean shouldSideBeRendered(IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
    if (side.getAxis() != EnumFacing.Axis.Y) {
      // Special case for walls painted with transparent/translucent textures
      IBlockState blockState2 = worldIn.getBlockState(pos);
      if (blockState2.getBlock() instanceof BlockPaintedWall
          && getPaintSource(blockState2, worldIn, pos) == getPaintSource(blockState2, worldIn, pos.offset(side.getOpposite()))) {
        return false;
      }
    }
    return super.shouldSideBeRendered(worldIn, pos, side);
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
    return new BlockStateWrapper(state, world, pos);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public IRenderMapper getRenderMapper() {
    return this;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public Pair<List<IBlockState>, List<IBakedModel>> mapBlockRender(BlockStateWrapper state, IBlockAccess world, BlockPos pos) {
    IBlockState paintSource = getPaintSource(state, world, pos);
    if (paintSource != null && paintSource.getBlock().canRenderInLayer(MinecraftForgeClient.getRenderLayer())) {
      return Pair.of(null, Collections.singletonList(mapRender(state, paintSource)));
    } else {
      return null;
    }
  }

  @SuppressWarnings("deprecation")
  @SideOnly(Side.CLIENT)
  private IBakedModel mapRender(IBlockState state, IBlockState paint) {

    Boolean up = state.getValue(UP);
    Boolean north = state.getValue(NORTH);
    Boolean east = state.getValue(EAST);
    Boolean west = state.getValue(WEST);
    Boolean south = state.getValue(SOUTH);

    if (!east && !north && !south && !up && !west) {
      return PaintRegistry.getModel(IBakedModel.class, "wall_post", paint, new UVLock(null));
    } else if (!east && north && !south && !up && !west) {
      return PaintRegistry.getModel(IBakedModel.class, "wall_n", paint, new UVLock(null));
    } else if (east && !north && !south && !up && !west) {
      return PaintRegistry.getModel(IBakedModel.class, "wall_n", paint, new UVLock(ModelRotation.X0_Y90));
    } else if (!east && !north && south && !up && !west) {
      return PaintRegistry.getModel(IBakedModel.class, "wall_n", paint, new UVLock(ModelRotation.X0_Y180));
    } else if (!east && !north && !south && !up && west) {
      return PaintRegistry.getModel(IBakedModel.class, "wall_n", paint, new UVLock(ModelRotation.X0_Y270));
    } else if (east && north && !south && !up && !west) {
      return PaintRegistry.getModel(IBakedModel.class, "wall_ne", paint, new UVLock(null));
    } else if (east && !north && south && !up && !west) {
      return PaintRegistry.getModel(IBakedModel.class, "wall_ne", paint, new UVLock(ModelRotation.X0_Y90));
    } else if (!east && !north && south && !up && west) {
      return PaintRegistry.getModel(IBakedModel.class, "wall_ne", paint, new UVLock(ModelRotation.X0_Y180));
    } else if (!east && north && !south && !up && west) {
      return PaintRegistry.getModel(IBakedModel.class, "wall_ne", paint, new UVLock(ModelRotation.X0_Y270));
    } else if (!east && north && south && !up && !west) {
      return PaintRegistry.getModel(IBakedModel.class, "wall_ns", paint, new UVLock(null));
    } else if (east && !north && !south && !up && west) {
      return PaintRegistry.getModel(IBakedModel.class, "wall_ns", paint, new UVLock(ModelRotation.X0_Y90));
    } else if (east && north && south && !up && !west) {
      return PaintRegistry.getModel(IBakedModel.class, "wall_nse", paint, new UVLock(null));
    } else if (east && !north && south && !up && west) {
      return PaintRegistry.getModel(IBakedModel.class, "wall_nse", paint, new UVLock(ModelRotation.X0_Y90));
    } else if (!east && north && south && !up && west) {
      return PaintRegistry.getModel(IBakedModel.class, "wall_nse", paint, new UVLock(ModelRotation.X0_Y180));
    } else if (east && north && !south && !up && west) {
      return PaintRegistry.getModel(IBakedModel.class, "wall_nse", paint, new UVLock(ModelRotation.X0_Y270));
    } else if (east && north && south && !up && west) {
      return PaintRegistry.getModel(IBakedModel.class, "wall_nsew", paint, new UVLock(null));
    } else if (!east && !north && !south && up && !west) {
      return PaintRegistry.getModel(IBakedModel.class, "wall_post", paint, new UVLock(null));
    } else if (!east && north && !south && up && !west) {
      return PaintRegistry.getModel(IBakedModel.class, "wall_n", paint, new UVLock(null));
    } else if (east && !north && !south && up && !west) {
      return PaintRegistry.getModel(IBakedModel.class, "wall_n", paint, new UVLock(ModelRotation.X0_Y90));
    } else if (!east && !north && south && up && !west) {
      return PaintRegistry.getModel(IBakedModel.class, "wall_n", paint, new UVLock(ModelRotation.X0_Y180));
    } else if (!east && !north && !south && up && west) {
      return PaintRegistry.getModel(IBakedModel.class, "wall_n", paint, new UVLock(ModelRotation.X0_Y270));
    } else if (east && north && !south && up && !west) {
      return PaintRegistry.getModel(IBakedModel.class, "wall_ne", paint, new UVLock(null));
    } else if (east && !north && south && up && !west) {
      return PaintRegistry.getModel(IBakedModel.class, "wall_ne", paint, new UVLock(ModelRotation.X0_Y90));
    } else if (!east && !north && south && up && west) {
      return PaintRegistry.getModel(IBakedModel.class, "wall_ne", paint, new UVLock(ModelRotation.X0_Y180));
    } else if (!east && north && !south && up && west) {
      return PaintRegistry.getModel(IBakedModel.class, "wall_ne", paint, new UVLock(ModelRotation.X0_Y270));
    } else if (!east && north && south && up && !west) {
      return PaintRegistry.getModel(IBakedModel.class, "wall_ns_above", paint, new UVLock(null));
    } else if (east && !north && !south && up && west) {
      return PaintRegistry.getModel(IBakedModel.class, "wall_ns_above", paint, new UVLock(ModelRotation.X0_Y90));
    } else if (east && north && south && up && !west) {
      return PaintRegistry.getModel(IBakedModel.class, "wall_nse", paint, new UVLock(null));
    } else if (east && !north && south && up && west) {
      return PaintRegistry.getModel(IBakedModel.class, "wall_nse", paint, new UVLock(ModelRotation.X0_Y90));
    } else if (!east && north && south && up && west) {
      return PaintRegistry.getModel(IBakedModel.class, "wall_nse", paint, new UVLock(ModelRotation.X0_Y180));
    } else if (east && north && !south && up && west) {
      return PaintRegistry.getModel(IBakedModel.class, "wall_nse", paint, new UVLock(ModelRotation.X0_Y270));
    } else if (east && north && south && up && west) {
      return PaintRegistry.getModel(IBakedModel.class, "wall_nsew", paint, new UVLock(null));
    }
      return null;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public List<IBlockState> mapOverlayLayer(BlockStateWrapper state, IBlockAccess world, BlockPos pos) {
    return null;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public Pair<List<IBlockState>, List<IBakedModel>> mapItemRender(Block block, ItemStack stack) {
    IBlockState paintSource = getPaintSource(block, stack);
    if (paintSource != null) {
      IBlockState stdOverlay = BlockMachineBase.block.getDefaultState().withProperty(EnumRenderPart.SUB, EnumRenderPart.PAINT_OVERLAY);
      @SuppressWarnings("deprecation")
      IBakedModel model1 = PaintRegistry.getModel(IBakedModel.class, "wall_inventory", paintSource, new UVLock(null));
      IBakedModel model2 = PaintRegistry.getModel(IBakedModel.class, "wall_inventory", stdOverlay, PaintRegistry.OVERLAY_TRANSFORMATION2);
      List<IBakedModel> list = new ArrayList<IBakedModel>();
      list.add(model1);
      list.add(model2);
      return Pair.of(null, list);
    } else {
      return null;
    }
  }

  @Override
  @SideOnly(Side.CLIENT)
  public Pair<List<IBlockState>, List<IBakedModel>> mapItemPaintOverlayRender(Block block, ItemStack stack) {
    return null;
  }

  @Override
  public boolean canRenderInLayer(EnumWorldBlockLayer layer) {
    return true;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
    if (tab != null) {
      super.getSubBlocks(itemIn, tab, list);
    }
  }

}
