package crazypants.enderio.base.block.painted;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.BlockEnder;
import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.base.EnderIOTab;
import crazypants.enderio.base.block.darksteel.trapdoor.BlockDarkSteelTrapDoor;
import crazypants.enderio.base.init.IModObject;
import crazypants.enderio.base.init.ModObject;
import crazypants.enderio.base.paint.IPaintable;
import crazypants.enderio.base.paint.PainterUtil2;
import crazypants.enderio.base.paint.render.PaintHelper;
import crazypants.enderio.base.paint.render.PaintRegistry;
import crazypants.enderio.base.recipe.MachineRecipeRegistry;
import crazypants.enderio.base.recipe.painter.BasicPainterTemplate;
import crazypants.enderio.base.render.IBlockStateWrapper;
import crazypants.enderio.base.render.ICacheKey;
import crazypants.enderio.base.render.IRenderMapper;
import crazypants.enderio.base.render.ISmartRenderAwareBlock;
import crazypants.enderio.base.render.pipeline.BlockStateWrapperBase;
import crazypants.enderio.base.render.property.EnumRenderPart;
import crazypants.enderio.base.render.property.IOMode.EnumIOMode;
import crazypants.enderio.base.render.registry.SmartModelAttacher;
import crazypants.enderio.base.render.util.QuadCollector;
import crazypants.enderio.util.Prep;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
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
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockPaintedTrapDoor extends BlockDarkSteelTrapDoor implements ITileEntityProvider, IPaintable.ITexturePaintableBlock, ISmartRenderAwareBlock,
    IRenderMapper.IBlockRenderMapper.IRenderLayerAware, IRenderMapper.IItemRenderMapper.IItemModelMapper, IModObject.WithBlockItem {

  public static BlockPaintedTrapDoor create_wooden(@Nonnull IModObject modObject) {
    BlockPaintedTrapDoor result = new BlockPaintedTrapDoor(modObject, Material.WOOD, false);
    result.init(modObject);
    MachineRecipeRegistry.instance.registerRecipe(MachineRecipeRegistry.PAINTER, new BasicPainterTemplate<BlockPaintedTrapDoor>(result, Blocks.TRAPDOOR));
    return result;
  }

  public static BlockPaintedTrapDoor create_iron(@Nonnull IModObject modObject) {
    BlockPaintedTrapDoor result = new BlockPaintedTrapDoor(modObject, Material.IRON, false);
    result.init(modObject);
    MachineRecipeRegistry.instance.registerRecipe(MachineRecipeRegistry.PAINTER, new BasicPainterTemplate<BlockPaintedTrapDoor>(result, Blocks.IRON_TRAPDOOR));
    return result;
  }

  public static BlockPaintedTrapDoor create_dark(@Nonnull IModObject modObject) {
    BlockPaintedTrapDoor result = new BlockPaintedTrapDoor(modObject, Material.IRON, true);
    result.init(modObject);
    MachineRecipeRegistry.instance.registerRecipe(MachineRecipeRegistry.PAINTER,
        new BasicPainterTemplate<BlockPaintedTrapDoor>(result, ModObject.blockDarkSteelTrapdoor.getBlock()));
    return result;
  }

  protected BlockPaintedTrapDoor(@Nonnull IModObject modObject, Material materialIn, boolean isBlastResistant) {
    super(modObject, materialIn, isBlastResistant);
    Prep.setNoCreativeTab(this);
  }

  protected BlockDarkSteelTrapDoor init(@Nonnull IModObject modObject) {
    SmartModelAttacher.registerNoProps(this);
    PaintRegistry.registerModel("trapdoor_bottom", new ResourceLocation("minecraft", "block/wooden_trapdoor_bottom"), PaintRegistry.PaintMode.ALL_TEXTURES);
    PaintRegistry.registerModel("trapdoor_top", new ResourceLocation("minecraft", "block/wooden_trapdoor_top"), PaintRegistry.PaintMode.ALL_TEXTURES);
    PaintRegistry.registerModel("trapdoor_open", new ResourceLocation("minecraft", "block/wooden_trapdoor_open"), PaintRegistry.PaintMode.ALL_TEXTURES);
    return this;
  }

  @Override
  public Item createBlockItem(@Nonnull IModObject modObject) {
    return modObject.apply(new BlockItemPaintedBlock(this));
  }

  @Override
  public TileEntity createNewTileEntity(@Nonnull World world, int metadata) {
    return new TileEntityPaintedBlock();
  }

  @Override
  public void onBlockPlacedBy(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull EntityLivingBase player,
      @Nonnull ItemStack stack) {
    setPaintSource(state, world, pos, PainterUtil2.getSourceBlock(stack));
    if (!world.isRemote) {
      world.notifyBlockUpdate(pos, state, state, 3);
    }
  }

  @Override
  public boolean removedByPlayer(@Nonnull IBlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull EntityPlayer player, boolean willHarvest) {
    if (willHarvest) {
      return true;
    }
    return super.removedByPlayer(state, world, pos, player, willHarvest);
  }

  @Override
  public void harvestBlock(@Nonnull World worldIn, @Nonnull EntityPlayer player, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nullable TileEntity te,
      @Nonnull ItemStack stack) {
    super.harvestBlock(worldIn, player, pos, state, te, stack);
    super.removedByPlayer(state, worldIn, pos, player, true);
  }

  @Override
  public @Nonnull List<ItemStack> getDrops(@Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nonnull IBlockState state, int fortune) {
    List<ItemStack> drops = super.getDrops(world, pos, state, fortune);
    for (ItemStack drop : drops) {
      PainterUtil2.setSourceBlock(NullHelper.notnullM(drop, "null stack from getDrops()"), getPaintSource(state, world, pos));
    }
    return drops;
  }

  @Override
  public @Nonnull ItemStack getPickBlock(@Nonnull IBlockState state, @Nonnull RayTraceResult target, @Nonnull World world, @Nonnull BlockPos pos,
      @Nonnull EntityPlayer player) {
    final ItemStack pickBlock = super.getPickBlock(state, target, world, pos, player);
    PainterUtil2.setSourceBlock(pickBlock, getPaintSource(state, world, pos));
    return pickBlock;
  }

  @Override
  public void setPaintSource(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nullable IBlockState paintSource) {
    TileEntity te = world.getTileEntity(pos);
    if (te instanceof IPaintable.IPaintableTileEntity) {
      ((IPaintableTileEntity) te).setPaintSource(paintSource);
    }
  }

  @Override
  public void setPaintSource(@Nonnull Block block, @Nonnull ItemStack stack, @Nullable IBlockState paintSource) {
    PainterUtil2.setSourceBlock(stack, paintSource);
  }

  @Override
  public IBlockState getPaintSource(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos) {
    TileEntity te = BlockEnder.getAnyTileEntitySafe(world, pos);
    if (te instanceof IPaintable.IPaintableTileEntity) {
      return ((IPaintableTileEntity) te).getPaintSource();
    }
    return null;
  }

  @Override
  public IBlockState getPaintSource(@Nonnull Block block, @Nonnull ItemStack stack) {
    return PainterUtil2.getSourceBlock(stack);
  }

  @Override
  public @Nonnull IBlockState getExtendedState(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos) {
    IBlockStateWrapper blockStateWrapper = new BlockStateWrapperBase(state, world, pos, this);
    blockStateWrapper.addCacheKey(getPaintSource(state, world, pos)).addCacheKey(state.getValue(FACING)).addCacheKey(state.getValue(OPEN))
        .addCacheKey(state.getValue(HALF));
    blockStateWrapper.bakeModel();
    return blockStateWrapper;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public @Nonnull IItemRenderMapper getItemRenderMapper() {
    return this;
  }

  @SideOnly(Side.CLIENT)
  private IBakedModel mapRender(IBlockState state, @Nullable IBlockState paint) {
    EnumFacing facing = state.getValue(FACING);
    Boolean open = state.getValue(OPEN);
    DoorHalf half = state.getValue(HALF);

    String model;
    ModelRotation modelState;

    if (open) {
      model = "trapdoor_open";
      switch (facing) {
      case EAST:
        modelState = ModelRotation.X0_Y90;
        break;
      case SOUTH:
        modelState = ModelRotation.X0_Y180;
        break;
      case WEST:
        modelState = ModelRotation.X0_Y270;
        break;
      default:
        modelState = null;
      }
    } else if (half == DoorHalf.TOP) {
      model = "trapdoor_top";
      modelState = null;
    } else {
      model = "trapdoor_bottom";
      modelState = null;
    }

    return PaintRegistry.getModel(IBakedModel.class, model, paint, modelState);
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

    IBakedModel model1 = PaintRegistry.getModel(IBakedModel.class, "trapdoor_bottom", paintSource, null);
    IBakedModel model2 = PaintRegistry.getModel(IBakedModel.class, "trapdoor_bottom", stdOverlay, PaintRegistry.OVERLAY_TRANSFORMATION3);
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
  public void getSubBlocks(@Nonnull Item itemIn, @Nonnull CreativeTabs tab, @Nonnull NonNullList<ItemStack> list) {
    if (tab == EnderIOTab.tabNoTab) {
      super.getSubBlocks(itemIn, tab, list);
    }
  }

  @Override
  @SideOnly(Side.CLIENT)
  public List<IBlockState> mapBlockRender(@Nonnull IBlockStateWrapper state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, BlockRenderLayer blockLayer,
      @Nonnull QuadCollector quadCollector) {
    IBlockState paintSource = getPaintSource(state, world, pos);
    if (blockLayer == null || PainterUtil2.canRenderInLayer(paintSource, blockLayer)) {
      quadCollector.addFriendlybakedModel(blockLayer, mapRender(state, paintSource), paintSource, MathHelper.getPositionRandom(pos));
    }
    return null;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public EnumMap<EnumFacing, EnumIOMode> mapOverlayLayer(@Nonnull IBlockStateWrapper state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos,
      boolean isPainted) {
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
