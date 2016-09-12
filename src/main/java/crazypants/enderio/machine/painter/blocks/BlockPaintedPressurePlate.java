package crazypants.enderio.machine.painter.blocks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.api.client.gui.IResourceTooltipProvider;
import com.enderio.core.common.BlockEnder;

import crazypants.enderio.EnderIO;
import crazypants.enderio.EnderIOTab;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.MachineRecipeRegistry;
import crazypants.enderio.machine.painter.blocks.BlockItemPaintedBlock.INamedSubBlocks;
import crazypants.enderio.machine.painter.recipe.PressurePlatePainterTemplate;
import crazypants.enderio.paint.IPaintable;
import crazypants.enderio.paint.PainterUtil2;
import crazypants.enderio.paint.render.PaintHelper;
import crazypants.enderio.paint.render.PaintRegistry;
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
import crazypants.enderio.waila.IWailaInfoProvider;
import crazypants.util.CapturedMob;
import info.loenwind.autosave.annotations.Storable;
import info.loenwind.autosave.annotations.Store;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBasePressurePlate;
import net.minecraft.block.BlockPressurePlateWeighted;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelRotation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockPaintedPressurePlate extends BlockBasePressurePlate
    implements ITileEntityProvider, IPaintable.ITexturePaintableBlock, ISmartRenderAwareBlock, IRenderMapper.IBlockRenderMapper.IRenderLayerAware,
    INamedSubBlocks, IResourceTooltipProvider, IWailaInfoProvider, IRenderMapper.IItemRenderMapper.IItemModelMapper {

  @Storable
  public static class TilePaintedPressurePlate extends TileEntityPaintedBlock {

    @Store
    private EnumPressurePlateType type = EnumPressurePlateType.WOOD;
    @Store
    private boolean silent = false;
    @Store
    private EnumFacing rotation = EnumFacing.NORTH;
    @Store
    private CapturedMob capturedMob = null;

    protected EnumPressurePlateType getType() {
      return type;
    }

    protected void setType(EnumPressurePlateType type) {
      this.type = type;
      markDirty();
    }

    protected boolean isSilent() {
      return silent;
    }

    protected void setSilent(boolean silent) {
      this.silent = silent;
      markDirty();
    }

    protected EnumFacing getRotation() {
      return rotation;
    }

    protected void setRotation(EnumFacing rotation) {
      if (rotation != EnumFacing.DOWN && rotation != EnumFacing.UP) {
        this.rotation = rotation;
        markDirty();
        updateBlock();
      }
    }

    protected CapturedMob getMobType() {
      return capturedMob;
    }

    protected void setMobType(CapturedMob capturedMob) {
      this.capturedMob = capturedMob;
    }

  }

  public static BlockPaintedPressurePlate create() {
    BlockPaintedPressurePlate result = new BlockPaintedPressurePlate(ModObject.blockPaintedPressurePlate.getUnlocalisedName());
    result.setHardness(0.5F);
    result.init();
    MachineRecipeRegistry.instance.registerRecipe(ModObject.blockPainter.getUnlocalisedName(),
        new PressurePlatePainterTemplate(result, EnumPressurePlateType.WOOD.getMetaFromType(), Blocks.WOODEN_PRESSURE_PLATE));
    MachineRecipeRegistry.instance.registerRecipe(ModObject.blockPainter.getUnlocalisedName(),
        new PressurePlatePainterTemplate(result, EnumPressurePlateType.STONE.getMetaFromType(), Blocks.STONE_PRESSURE_PLATE));
    MachineRecipeRegistry.instance.registerRecipe(ModObject.blockPainter.getUnlocalisedName(),
        new PressurePlatePainterTemplate(result, EnumPressurePlateType.IRON.getMetaFromType(), Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE));
    MachineRecipeRegistry.instance.registerRecipe(ModObject.blockPainter.getUnlocalisedName(),
        new PressurePlatePainterTemplate(result, EnumPressurePlateType.GOLD.getMetaFromType(), Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE));

    return result;
  }

  private final String name;

  public BlockPaintedPressurePlate(String name) {
    super(Material.IRON);
    this.setDefaultState(this.blockState.getBaseState().withProperty(BlockPressurePlateWeighted.POWER, 0));
    setCreativeTab(EnderIOTab.tabEnderIO);
    this.name = name;
    setUnlocalizedName(name);
    setRegistryName(name);
    setSoundType(SoundType.WOOD);
  }

  private final IBlockState[] defaultPaints = new IBlockState[EnumPressurePlateType.values().length];

  private void init() {
    GameRegistry.register(this);
    GameRegistry.register(new BlockItemPaintedPressurePlate(this, name));
    GameRegistry.registerTileEntity(TilePaintedPressurePlate.class, name + "TileEntity");
    SmartModelAttacher.registerNoProps(this);
    PaintRegistry.registerModel("pressure_plate_up", new ResourceLocation("minecraft", "block/stone_pressure_plate_up"), PaintRegistry.PaintMode.ALL_TEXTURES);
    PaintRegistry.registerModel("pressure_plate_down", new ResourceLocation("minecraft", "block/stone_pressure_plate_down"),
        PaintRegistry.PaintMode.ALL_TEXTURES);
    PaintRegistry.registerModel("pressure_plate_inventory", new ResourceLocation("minecraft", "block/stone_pressure_plate_up"),
        PaintRegistry.PaintMode.ALL_TEXTURES);

    defaultPaints[EnumPressurePlateType.WOOD.ordinal()] = Blocks.WOODEN_PRESSURE_PLATE.getDefaultState();
    defaultPaints[EnumPressurePlateType.STONE.ordinal()] = Blocks.STONE_PRESSURE_PLATE.getDefaultState();
    defaultPaints[EnumPressurePlateType.IRON.ordinal()] = Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE.getDefaultState();
    defaultPaints[EnumPressurePlateType.GOLD.ordinal()] = Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE.getDefaultState();
    defaultPaints[EnumPressurePlateType.DARKSTEEL.ordinal()] = getDefaultState().withProperty(BlockPressurePlateWeighted.POWER, 1);
    defaultPaints[EnumPressurePlateType.SOULARIUM.ordinal()] = getDefaultState().withProperty(BlockPressurePlateWeighted.POWER, 2);
    defaultPaints[EnumPressurePlateType.TUNED.ordinal()] = getDefaultState().withProperty(BlockPressurePlateWeighted.POWER, 3);
  }

  @Override
  public TileEntity createNewTileEntity(World world, int metadata) {
    return new TilePaintedPressurePlate();
  }

  @Override
  public IBlockState getStateFromMeta(int meta) {
    return this.getDefaultState().withProperty(BlockPressurePlateWeighted.POWER, meta);
  }

  @Override
  public int getMetaFromState(IBlockState state) {
    return state.getValue(BlockPressurePlateWeighted.POWER);
  }

  @Override
  protected BlockStateContainer createBlockState() {
    return new BlockStateContainer(this, new IProperty[] { BlockPressurePlateWeighted.POWER });
  }

  @Override
  protected int computeRedstoneStrength(World worldIn, BlockPos pos) {
    TileEntity te = BlockEnder.getAnyTileEntitySafe(worldIn, pos);
    if (te instanceof BlockPaintedPressurePlate.TilePaintedPressurePlate) {
      EnumPressurePlateType type = ((BlockPaintedPressurePlate.TilePaintedPressurePlate) te).getType();
      return type.getCountingMode()
          .count(worldIn.getEntitiesWithinAABB(type.getSearchClass(), PRESSURE_AABB.offset(pos), type.getPredicate(getMobType(worldIn, pos))));
    } else {
      return getRedstoneStrength(worldIn.getBlockState(pos));
    }
  }

  @Override
  protected int getRedstoneStrength(IBlockState state) {
    return state.getValue(BlockPressurePlateWeighted.POWER);
  }

  @Override
  protected IBlockState setRedstoneStrength(IBlockState state, int strength) {
    return state.withProperty(BlockPressurePlateWeighted.POWER, strength);
  }

  protected void setTypeFromMeta(IBlockAccess worldIn, BlockPos pos, int meta) {
    TileEntity te = worldIn.getTileEntity(pos);
    if (te instanceof BlockPaintedPressurePlate.TilePaintedPressurePlate) {
      ((BlockPaintedPressurePlate.TilePaintedPressurePlate) te).setType(EnumPressurePlateType.getTypeFromMeta(meta));
      ((BlockPaintedPressurePlate.TilePaintedPressurePlate) te).setSilent(EnumPressurePlateType.getSilentFromMeta(meta));
    }
  }

  protected int getMetaForStack(IBlockAccess worldIn, BlockPos pos) {
    TileEntity te = BlockEnder.getAnyTileEntitySafe(worldIn, pos);
    if (te instanceof BlockPaintedPressurePlate.TilePaintedPressurePlate) {
      return EnumPressurePlateType.getMetaFromType(((BlockPaintedPressurePlate.TilePaintedPressurePlate) te).getType(),
          ((BlockPaintedPressurePlate.TilePaintedPressurePlate) te).isSilent());
    }
    return 0;
  }

  protected EnumPressurePlateType getType(IBlockAccess worldIn, BlockPos pos) {
    TileEntity te = BlockEnder.getAnyTileEntitySafe(worldIn, pos);
    if (te instanceof BlockPaintedPressurePlate.TilePaintedPressurePlate) {
      return ((BlockPaintedPressurePlate.TilePaintedPressurePlate) te).getType();
    }
    return EnumPressurePlateType.WOOD;
  }

  protected boolean isSilent(IBlockAccess worldIn, BlockPos pos) {
    TileEntity te = BlockEnder.getAnyTileEntitySafe(worldIn, pos);
    if (te instanceof BlockPaintedPressurePlate.TilePaintedPressurePlate) {
      return ((BlockPaintedPressurePlate.TilePaintedPressurePlate) te).isSilent();
    }
    return false;
  }

  protected CapturedMob getMobType(IBlockAccess worldIn, BlockPos pos) {
    TileEntity te = BlockEnder.getAnyTileEntitySafe(worldIn, pos);
    if (te instanceof BlockPaintedPressurePlate.TilePaintedPressurePlate) {
      return ((BlockPaintedPressurePlate.TilePaintedPressurePlate) te).getMobType();
    }
    return null;
  }

  protected void setMobType(IBlockAccess worldIn, BlockPos pos, CapturedMob mobType) {
    TileEntity te = worldIn.getTileEntity(pos);
    if (te instanceof BlockPaintedPressurePlate.TilePaintedPressurePlate) {
      ((BlockPaintedPressurePlate.TilePaintedPressurePlate) te).setMobType(mobType);
    }
  }

  @Override
  public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
    return getDefaultState();
  }

  @Override
  public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
    setTypeFromMeta(worldIn, pos, stack.getMetadata());
    setPaintSource(state, worldIn, pos, PainterUtil2.getSourceBlock(stack));
    setRotation(worldIn, pos, EnumFacing.fromAngle(placer.rotationYaw));
    setMobType(worldIn, pos, CapturedMob.create(stack));
    if (!worldIn.isRemote) {
      worldIn.notifyBlockUpdate(pos, state, state, 3);
    }
  }

  @Override
  public boolean rotateBlock(World world, BlockPos pos, EnumFacing axis) {
    setRotation(world, pos, getRotation(world, pos).rotateAround(EnumFacing.Axis.Y));
    return true;
  }

  @Override
  public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
    if (willHarvest) {
      return true;
    }
    return super.removedByPlayer(state, world, pos, player, willHarvest);
  }

  @Override
  public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, @Nullable ItemStack item) {
    super.harvestBlock(worldIn, player, pos, state, te, item);
    super.removedByPlayer(state, worldIn, pos, player, true);
  }

  @Override
  public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
    return Collections.singletonList(getDrop(world, pos));
  }

  protected ItemStack getDrop(IBlockAccess world, BlockPos pos) {
    CapturedMob mobType = getMobType(world, pos);
    ItemStack drop = mobType != null ? mobType.toStack(Item.getItemFromBlock(this), getMetaForStack(world, pos), 1)
        : new ItemStack(Item.getItemFromBlock(this), 1, getMetaForStack(world, pos));
    PainterUtil2.setSourceBlock(drop, getPaintSource(null, world, pos));
    return drop;
  }

  @Override
  public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
    return getDrop(world, pos);
  }

  @Override
  public void setPaintSource(IBlockState state, IBlockAccess world, BlockPos pos, @Nullable IBlockState paintSource) {
    TileEntity te = world.getTileEntity(pos);
    if (te instanceof IPaintable.IPaintableTileEntity) {
      if (defaultPaints[getType(world, pos).ordinal()] == paintSource) {
        ((IPaintableTileEntity) te).setPaintSource(null);
      } else {
        ((IPaintableTileEntity) te).setPaintSource(paintSource);
      }
    }
  }

  @Override
  public void setPaintSource(Block block, ItemStack stack, @Nullable IBlockState paintSource) {
    if (defaultPaints[EnumPressurePlateType.getTypeFromMeta(stack.getMetadata()).ordinal()] == paintSource) {
      PainterUtil2.setSourceBlock(stack, null);
    } else {
      PainterUtil2.setSourceBlock(stack, paintSource);
    }
  }

  @Override
  public IBlockState getPaintSource(IBlockState state, IBlockAccess world, BlockPos pos) {
    TileEntity te = BlockEnder.getAnyTileEntitySafe(world, pos);
    if (te instanceof IPaintable.IPaintableTileEntity) {
      IBlockState paintSource = ((IPaintableTileEntity) te).getPaintSource();
      if (paintSource != null) {
        return paintSource;
      }
    }
    return defaultPaints[getType(world, pos).ordinal()];
  }

  @Override
  public IBlockState getPaintSource(Block block, ItemStack stack) {
    IBlockState paintSource = PainterUtil2.getSourceBlock(stack);
    return paintSource != null ? paintSource : defaultPaints[EnumPressurePlateType.getTypeFromMeta(stack.getMetadata()).ordinal()];
  }

  @Override
  public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
    if (state != null && world != null && pos != null) {
      IBlockStateWrapper blockStateWrapper = new BlockStateWrapperBase(state, world, pos, this);
      blockStateWrapper.addCacheKey(getPaintSource(state, world, pos)).addCacheKey(getRotation(world, pos))
          .addCacheKey(state.getValue(BlockPressurePlateWeighted.POWER) > 0);
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
  private IBakedModel mapRender(IBlockState state, @Nullable IBlockState paint, EnumFacing facing) {

    ModelRotation rot;
    switch (facing) {
    case EAST:
      rot = ModelRotation.X0_Y90;
      break;
    case NORTH:
      rot = null;
      break;
    case SOUTH:
      rot = ModelRotation.X0_Y180;
      break;
    case WEST:
      rot = ModelRotation.X0_Y270;
      break;
    default:
      return null;
    }

    if (state.getValue(BlockPressurePlateWeighted.POWER) > 0) {
      return PaintRegistry.getModel(IBakedModel.class, "pressure_plate_down", paint, rot);
    } else {
      return PaintRegistry.getModel(IBakedModel.class, "pressure_plate_up", paint, rot);
    }
  }

  protected EnumFacing getRotation(IBlockAccess world, BlockPos pos) {
    TileEntity te = BlockEnder.getAnyTileEntitySafe(world, pos);
    if (te instanceof TilePaintedPressurePlate) {
      return ((TilePaintedPressurePlate) te).getRotation();
    }
    return EnumFacing.NORTH;
  }

  protected void setRotation(IBlockAccess world, BlockPos pos, EnumFacing rotation) {
    TileEntity te = world.getTileEntity(pos);
    if (te instanceof TilePaintedPressurePlate) {
      ((TilePaintedPressurePlate) te).setRotation(rotation);
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
    IBakedModel model1 = PaintRegistry.getModel(IBakedModel.class, "pressure_plate_inventory", paintSource, null);
    List<IBakedModel> list = new ArrayList<IBakedModel>();
    list.add(model1);
    if (paintSource != defaultPaints[EnumPressurePlateType.getTypeFromMeta(stack.getMetadata()).ordinal()]) {
      IBlockState stdOverlay = BlockMachineBase.block.getDefaultState().withProperty(EnumRenderPart.SUB, EnumRenderPart.PAINT_OVERLAY);
      IBakedModel model2 = PaintRegistry.getModel(IBakedModel.class, "pressure_plate_inventory", stdOverlay, PaintRegistry.OVERLAY_TRANSFORMATION);
      list.add(model2);
    }
    return list;
  }

  @Override
  public boolean canRenderInLayer(BlockRenderLayer layer) {
    return true;
  }

  @Override
  public int getFlammability(IBlockAccess world, BlockPos pos, EnumFacing face) {
    TileEntity te = BlockEnder.getAnyTileEntitySafe(world, pos);
    if (te instanceof BlockPaintedPressurePlate.TilePaintedPressurePlate) {
      return EnumPressurePlateType.WOOD == ((BlockPaintedPressurePlate.TilePaintedPressurePlate) te).getType() ? 20 : 0;
    }
    return 0;
  }

  @Override
  public int getFireSpreadSpeed(IBlockAccess world, BlockPos pos, EnumFacing face) {
    TileEntity te = BlockEnder.getAnyTileEntitySafe(world, pos);
    if (te instanceof BlockPaintedPressurePlate.TilePaintedPressurePlate) {
      return EnumPressurePlateType.WOOD == ((BlockPaintedPressurePlate.TilePaintedPressurePlate) te).getType() ? 5 : 0;
    }
    return 0;
  }

  @Override
  public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
    for (EnumPressurePlateType type : EnumPressurePlateType.values()) {
      if (tab == EnderIOTab.tabNoTab || type.ordinal() >= EnumPressurePlateType.DARKSTEEL.ordinal()) {
        list.add(new ItemStack(itemIn, 1, EnumPressurePlateType.getMetaFromType(type, false)));
      }
      list.add(new ItemStack(itemIn, 1, EnumPressurePlateType.getMetaFromType(type, true)));
    }
  }

  @Override
  protected void updateState(World worldIn, BlockPos pos, IBlockState state, int oldRedstoneStrength) {
    int newRedstoneStrength = this.computeRedstoneStrength(worldIn, pos);
    boolean wasOn = oldRedstoneStrength > 0;
    boolean isOn = newRedstoneStrength > 0;

    if (oldRedstoneStrength != newRedstoneStrength) {
      state = this.setRedstoneStrength(state, newRedstoneStrength);
      worldIn.setBlockState(pos, state, 2);
      this.updateNeighbors(worldIn, pos);
      worldIn.markBlockRangeForRenderUpdate(pos, pos);

      if (!isSilent(worldIn, pos)) {
        if (!isOn && wasOn) {
          worldIn.playSound(pos.getX() + 0.5D, pos.getY() + 0.1D, pos.getZ() + 0.5D, SoundEvents.UI_BUTTON_CLICK, SoundCategory.BLOCKS, 0.3F, 0.5F, false);
        } else if (isOn && !wasOn) {
          worldIn.playSound(pos.getX() + 0.5D, pos.getY() + 0.1D, pos.getZ() + 0.5D, SoundEvents.UI_BUTTON_CLICK, SoundCategory.BLOCKS, 0.3F, 0.6F, false);
        }
      }
    }

    if (isOn) {
      worldIn.scheduleUpdate(new BlockPos(pos), this, this.tickRate(worldIn));
    }
  }

  public static class BlockItemPaintedPressurePlate extends BlockItemPaintedBlock {

    public BlockItemPaintedPressurePlate(BlockPaintedPressurePlate block, String name) {
      super(block, name);      
    }

    @Override
    public boolean hasEffect(ItemStack stack) {
      return EnumPressurePlateType.getTypeFromMeta(stack.getMetadata()) == EnumPressurePlateType.TUNED;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
      super.addInformation(stack, playerIn, tooltip, advanced);
      CapturedMob capturedMob = CapturedMob.create(stack);
      if (capturedMob != null) {
        tooltip.add(EnderIO.lang.localize("tile.plockPaintedPressurePlate.tuned", capturedMob.getDisplayName()));
      }
    }

  }

  @Override
  public String getUnlocalizedName(int meta) {
    return getUnlocalizedName() + "." + EnumPressurePlateType.getTypeFromMeta(meta).getName()
        + (EnumPressurePlateType.getSilentFromMeta(meta) ? ".silent" : "");
  }

  @Override
  public String getUnlocalizedNameForTooltip(ItemStack itemStack) {
    return getUnlocalizedName(itemStack.getMetadata());
  }

  @Override
  public void getWailaInfo(List<String> tooltip, EntityPlayer player, World world, int x, int y, int z) {
    ItemStack drop = getDrop(world, new BlockPos(x, y, z));
    if (drop != null) {
      tooltip.add(PainterUtil2.getTooltTipText(drop));
      CapturedMob capturedMob = CapturedMob.create(drop);
      if (capturedMob != null) {
        tooltip.add(EnderIO.lang.localize("tile.plockPaintedPressurePlate.tuned", capturedMob.getDisplayName()));
      }
    }
  }

  @Override
  public int getDefaultDisplayMask(World world, int x, int y, int z) {
    return 0;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public List<IBlockState> mapBlockRender(IBlockStateWrapper state, IBlockAccess world, BlockPos pos, BlockRenderLayer blockLayer,
      QuadCollector quadCollector) {
    IBlockState paintSource = getPaintSource(state, world, pos);
    if (PainterUtil2.canRenderInLayer(paintSource, blockLayer) && (paintSource == null || paintSource.getBlock() != EnderIO.blockFusedQuartz)) {
      quadCollector.addFriendlybakedModel(blockLayer, mapRender(state, paintSource, getRotation(world, pos)), paintSource, MathHelper.getPositionRandom(pos));
    }
    return null;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public EnumMap<EnumFacing, EnumIOMode> mapOverlayLayer(IBlockStateWrapper state, IBlockAccess world, BlockPos pos, boolean isPainted) {
    return null;
  }

  @Override
  protected void playClickOnSound(World worldIn, BlockPos color) {
    if (blockMaterial == Material.WOOD) {
      worldIn.playSound((EntityPlayer) null, color, SoundEvents.BLOCK_WOOD_PRESSPLATE_CLICK_ON, SoundCategory.BLOCKS, 0.3F, 0.8F);
    } else {
      worldIn.playSound((EntityPlayer) null, color, SoundEvents.BLOCK_STONE_PRESSPLATE_CLICK_ON, SoundCategory.BLOCKS, 0.3F, 0.6F);
    }

  }

  @Override
  protected void playClickOffSound(World worldIn, BlockPos pos) {
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
