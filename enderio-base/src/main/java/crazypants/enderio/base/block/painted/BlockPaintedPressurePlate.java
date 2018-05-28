package crazypants.enderio.base.block.painted;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.api.client.gui.IResourceTooltipProvider;
import com.enderio.core.common.BlockEnder;
import com.enderio.core.common.transform.EnderCoreMethods.IOverlayRenderAware;
import com.enderio.core.common.util.NNList;

import crazypants.enderio.base.block.painted.BlockItemPaintedBlock.INamedSubBlocks;
import crazypants.enderio.base.init.IModObject;
import crazypants.enderio.base.init.ModObject;
import crazypants.enderio.base.lang.Lang;
import crazypants.enderio.base.material.glass.BlockFusedQuartzBase;
import crazypants.enderio.base.paint.IPaintable;
import crazypants.enderio.base.paint.PaintUtil;
import crazypants.enderio.base.paint.render.PaintHelper;
import crazypants.enderio.base.paint.render.PaintRegistry;
import crazypants.enderio.base.recipe.MachineRecipeRegistry;
import crazypants.enderio.base.recipe.painter.PressurePlatePainterTemplate;
import crazypants.enderio.base.render.IBlockStateWrapper;
import crazypants.enderio.base.render.ICacheKey;
import crazypants.enderio.base.render.ICustomSubItems;
import crazypants.enderio.base.render.IRenderMapper;
import crazypants.enderio.base.render.ISmartRenderAwareBlock;
import crazypants.enderio.base.render.itemoverlay.MobNameOverlayRenderHelper;
import crazypants.enderio.base.render.pipeline.BlockStateWrapperBase;
import crazypants.enderio.base.render.property.EnumRenderPart;
import crazypants.enderio.base.render.registry.SmartModelAttacher;
import crazypants.enderio.base.render.util.QuadCollector;
import crazypants.enderio.util.CapturedMob;
import crazypants.enderio.util.Prep;
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
import net.minecraft.client.util.ITooltipFlag;
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
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockPaintedPressurePlate extends BlockBasePressurePlate
    implements ITileEntityProvider, IPaintable.ITexturePaintableBlock, ISmartRenderAwareBlock, IRenderMapper.IBlockRenderMapper.IRenderLayerAware,
    INamedSubBlocks, IResourceTooltipProvider, IRenderMapper.IItemRenderMapper.IItemModelMapper, IModObject.WithBlockItem, ICustomSubItems {

  public static BlockPaintedPressurePlate create(@Nonnull IModObject modObject) {
    BlockPaintedPressurePlate result = new BlockPaintedPressurePlate(modObject);
    result.setHardness(0.5F);
    result.init(modObject);
    MachineRecipeRegistry.instance.registerRecipe(MachineRecipeRegistry.PAINTER,
        new PressurePlatePainterTemplate(result, EnumPressurePlateType.WOOD.getMetaFromType(), Blocks.WOODEN_PRESSURE_PLATE));
    MachineRecipeRegistry.instance.registerRecipe(MachineRecipeRegistry.PAINTER,
        new PressurePlatePainterTemplate(result, EnumPressurePlateType.STONE.getMetaFromType(), Blocks.STONE_PRESSURE_PLATE));
    MachineRecipeRegistry.instance.registerRecipe(MachineRecipeRegistry.PAINTER,
        new PressurePlatePainterTemplate(result, EnumPressurePlateType.IRON.getMetaFromType(), Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE));
    MachineRecipeRegistry.instance.registerRecipe(MachineRecipeRegistry.PAINTER,
        new PressurePlatePainterTemplate(result, EnumPressurePlateType.GOLD.getMetaFromType(), Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE));

    return result;
  }

  public BlockPaintedPressurePlate(@Nonnull IModObject modObject) {
    super(Material.IRON);
    this.setDefaultState(getBlockState().getBaseState().withProperty(BlockPressurePlateWeighted.POWER, 0));
    Prep.setNoCreativeTab(this);
    modObject.apply(this);
    setSoundType(SoundType.WOOD);
  }

  private final NNList<IBlockState> defaultPaints = new NNList<IBlockState>(EnumPressurePlateType.values().length,
      Blocks.WOODEN_PRESSURE_PLATE.getDefaultState());

  private void init(@Nonnull IModObject modObject) {
    SmartModelAttacher.registerNoProps(this);
    PaintRegistry.registerModel("pressure_plate_up", new ResourceLocation("minecraft", "block/stone_pressure_plate_up"), PaintRegistry.PaintMode.ALL_TEXTURES);
    PaintRegistry.registerModel("pressure_plate_down", new ResourceLocation("minecraft", "block/stone_pressure_plate_down"),
        PaintRegistry.PaintMode.ALL_TEXTURES);
    PaintRegistry.registerModel("pressure_plate_inventory", new ResourceLocation("minecraft", "block/stone_pressure_plate_up"),
        PaintRegistry.PaintMode.ALL_TEXTURES);

    defaultPaints.set(EnumPressurePlateType.WOOD.ordinal(), Blocks.WOODEN_PRESSURE_PLATE.getDefaultState());
    defaultPaints.set(EnumPressurePlateType.STONE.ordinal(), Blocks.STONE_PRESSURE_PLATE.getDefaultState());
    defaultPaints.set(EnumPressurePlateType.IRON.ordinal(), Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE.getDefaultState());
    defaultPaints.set(EnumPressurePlateType.GOLD.ordinal(), Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE.getDefaultState());
    defaultPaints.set(EnumPressurePlateType.DARKSTEEL.ordinal(), getDefaultState().withProperty(BlockPressurePlateWeighted.POWER, 1));
    defaultPaints.set(EnumPressurePlateType.SOULARIUM.ordinal(), getDefaultState().withProperty(BlockPressurePlateWeighted.POWER, 2));
    defaultPaints.set(EnumPressurePlateType.TUNED.ordinal(), getDefaultState().withProperty(BlockPressurePlateWeighted.POWER, 3));
  }

  @Override
  public Item createBlockItem(@Nonnull IModObject modObject) {
    return modObject.apply(new BlockItemPaintedPressurePlate(this));
  }

  @Override
  public TileEntity createNewTileEntity(@Nonnull World world, int metadata) {
    return new TilePaintedPressurePlate();
  }

  @Override
  public @Nonnull IBlockState getStateFromMeta(int meta) {
    return this.getDefaultState().withProperty(BlockPressurePlateWeighted.POWER, meta);
  }

  @Override
  public int getMetaFromState(@Nonnull IBlockState state) {
    return state.getValue(BlockPressurePlateWeighted.POWER);
  }

  @Override
  protected @Nonnull BlockStateContainer createBlockState() {
    return new BlockStateContainer(this, new IProperty[] { BlockPressurePlateWeighted.POWER });
  }

  @Override
  protected int computeRedstoneStrength(@Nonnull World worldIn, @Nonnull BlockPos pos) {
    TileEntity te = BlockEnder.getAnyTileEntitySafe(worldIn, pos);
    if (te instanceof TilePaintedPressurePlate) {
      EnumPressurePlateType type = ((TilePaintedPressurePlate) te).getType();
      return type.getCountingMode()
          .count(worldIn.getEntitiesWithinAABB(type.getSearchClass(), PRESSURE_AABB.offset(pos), type.getPredicate(getMobType(worldIn, pos))));
    } else {
      return getRedstoneStrength(worldIn.getBlockState(pos));
    }
  }

  @Override
  protected int getRedstoneStrength(@Nonnull IBlockState state) {
    return state.getValue(BlockPressurePlateWeighted.POWER);
  }

  @Override
  protected @Nonnull IBlockState setRedstoneStrength(@Nonnull IBlockState state, int strength) {
    return state.withProperty(BlockPressurePlateWeighted.POWER, strength);
  }

  protected void setTypeFromMeta(@Nonnull IBlockAccess worldIn, @Nonnull BlockPos pos, int meta) {
    TileEntity te = worldIn.getTileEntity(pos);
    if (te instanceof TilePaintedPressurePlate) {
      ((TilePaintedPressurePlate) te).setType(EnumPressurePlateType.getTypeFromMeta(meta));
      ((TilePaintedPressurePlate) te).setSilent(EnumPressurePlateType.getSilentFromMeta(meta));
    }
  }

  protected int getMetaForStack(@Nonnull IBlockAccess worldIn, @Nonnull BlockPos pos) {
    TileEntity te = BlockEnder.getAnyTileEntitySafe(worldIn, pos);
    if (te instanceof TilePaintedPressurePlate) {
      return EnumPressurePlateType.getMetaFromType(((TilePaintedPressurePlate) te).getType(), ((TilePaintedPressurePlate) te).isSilent());
    }
    return 0;
  }

  protected EnumPressurePlateType getType(@Nonnull IBlockAccess worldIn, @Nonnull BlockPos pos) {
    TileEntity te = BlockEnder.getAnyTileEntitySafe(worldIn, pos);
    if (te instanceof TilePaintedPressurePlate) {
      return ((TilePaintedPressurePlate) te).getType();
    }
    return EnumPressurePlateType.WOOD;
  }

  protected boolean isSilent(@Nonnull IBlockAccess worldIn, @Nonnull BlockPos pos) {
    TileEntity te = BlockEnder.getAnyTileEntitySafe(worldIn, pos);
    if (te instanceof TilePaintedPressurePlate) {
      return ((TilePaintedPressurePlate) te).isSilent();
    }
    return false;
  }

  protected CapturedMob getMobType(@Nonnull IBlockAccess worldIn, @Nonnull BlockPos pos) {
    TileEntity te = BlockEnder.getAnyTileEntitySafe(worldIn, pos);
    if (te instanceof TilePaintedPressurePlate) {
      return ((TilePaintedPressurePlate) te).getMobType();
    }
    return null;
  }

  protected void setMobType(IBlockAccess worldIn, @Nonnull BlockPos pos, CapturedMob mobType) {
    TileEntity te = worldIn.getTileEntity(pos);
    if (te instanceof TilePaintedPressurePlate) {
      ((TilePaintedPressurePlate) te).setMobType(mobType);
    }
  }

  @Override
  public @Nonnull IBlockState getStateForPlacement(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull EnumFacing facing, float hitX, float hitY,
      float hitZ, int meta, @Nonnull EntityLivingBase placer) {
    return getDefaultState();
  }

  @Override
  public void onBlockPlacedBy(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull EntityLivingBase placer,
      @Nonnull ItemStack stack) {
    setTypeFromMeta(worldIn, pos, stack.getMetadata());
    setPaintSource(state, worldIn, pos, PaintUtil.getSourceBlock(stack));
    setRotation(worldIn, pos, EnumFacing.fromAngle(placer.rotationYaw));
    setMobType(worldIn, pos, CapturedMob.create(stack));
    if (!worldIn.isRemote) {
      worldIn.notifyBlockUpdate(pos, state, state, 3);
    }
  }

  @Override
  public boolean rotateBlock(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull EnumFacing axis) {
    setRotation(world, pos, getRotation(world, pos).rotateAround(EnumFacing.Axis.Y));
    return true;
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
      @Nonnull ItemStack item) {
    super.harvestBlock(worldIn, player, pos, state, te, item);
    super.removedByPlayer(state, worldIn, pos, player, true);
  }

  @Override
  public void getDrops(@Nonnull NonNullList<ItemStack> drops, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nonnull IBlockState state, int fortune) {
    drops.add(getDrop(world, pos));
  }

  protected @Nonnull ItemStack getDrop(@Nonnull IBlockAccess world, @Nonnull BlockPos pos) {
    CapturedMob mobType = getMobType(world, pos);
    ItemStack drop = mobType != null ? mobType.toStack(Item.getItemFromBlock(this), getMetaForStack(world, pos), 1)
        : new ItemStack(Item.getItemFromBlock(this), 1, getMetaForStack(world, pos));
    PaintUtil.setSourceBlock(drop, getPaintSource(world.getBlockState(pos), world, pos));
    return drop;
  }

  @Override
  public @Nonnull ItemStack getPickBlock(@Nonnull IBlockState state, @Nonnull RayTraceResult target, @Nonnull World world, @Nonnull BlockPos pos,
      @Nonnull EntityPlayer player) {
    return getDrop(world, pos);
  }

  @Override
  public void setPaintSource(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nullable IBlockState paintSource) {
    TileEntity te = world.getTileEntity(pos);
    if (te instanceof IPaintable.IPaintableTileEntity) {
      if (defaultPaints.get(getType(world, pos).ordinal()) == paintSource) {
        ((IPaintableTileEntity) te).setPaintSource(null);
      } else {
        ((IPaintableTileEntity) te).setPaintSource(paintSource);
      }
    }
  }

  @Override
  public void setPaintSource(@Nonnull Block block, @Nonnull ItemStack stack, @Nullable IBlockState paintSource) {
    if (defaultPaints.get(EnumPressurePlateType.getTypeFromMeta(stack.getMetadata()).ordinal()) == paintSource) {
      PaintUtil.setSourceBlock(stack, null);
    } else {
      PaintUtil.setSourceBlock(stack, paintSource);
    }
  }

  @Override
  public IBlockState getPaintSource(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos) {
    TileEntity te = BlockEnder.getAnyTileEntitySafe(world, pos);
    if (te instanceof IPaintable.IPaintableTileEntity) {
      IBlockState paintSource = ((IPaintableTileEntity) te).getPaintSource();
      if (paintSource != null) {
        return paintSource;
      }
    }
    return defaultPaints.get(getType(world, pos).ordinal());
  }

  @Override
  public IBlockState getPaintSource(@Nonnull Block block, @Nonnull ItemStack stack) {
    IBlockState paintSource = PaintUtil.getSourceBlock(stack);
    return paintSource != null ? paintSource : defaultPaints.get(EnumPressurePlateType.getTypeFromMeta(stack.getMetadata()).ordinal());
  }

  @Override
  public @Nonnull IBlockState getExtendedState(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos) {
    IBlockStateWrapper blockStateWrapper = new BlockStateWrapperBase(state, world, pos, this);
    blockStateWrapper.addCacheKey(getPaintSource(state, world, pos)).addCacheKey(getRotation(world, pos))
        .addCacheKey(state.getValue(BlockPressurePlateWeighted.POWER) > 0);
    blockStateWrapper.bakeModel();
    return blockStateWrapper;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public @Nonnull IItemRenderMapper getItemRenderMapper() {
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

  protected EnumFacing getRotation(@Nonnull IBlockAccess world, @Nonnull BlockPos pos) {
    TileEntity te = BlockEnder.getAnyTileEntitySafe(world, pos);
    if (te instanceof TilePaintedPressurePlate) {
      return ((TilePaintedPressurePlate) te).getRotation();
    }
    return EnumFacing.NORTH;
  }

  protected void setRotation(IBlockAccess world, @Nonnull BlockPos pos, @Nonnull EnumFacing rotation) {
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
  public List<IBakedModel> mapItemRender(@Nonnull Block block, @Nonnull ItemStack stack) {
    IBlockState paintSource = getPaintSource(block, stack);
    IBakedModel model1 = PaintRegistry.getModel(IBakedModel.class, "pressure_plate_inventory", paintSource, null);
    List<IBakedModel> list = new ArrayList<IBakedModel>();
    list.add(model1);
    if (paintSource != defaultPaints.get(EnumPressurePlateType.getTypeFromMeta(stack.getMetadata()).ordinal())) {
      IBlockState stdOverlay = ModObject.block_machine_base.getBlockNN().getDefaultState().withProperty(EnumRenderPart.SUB, EnumRenderPart.PAINT_OVERLAY);
      IBakedModel model2 = PaintRegistry.getModel(IBakedModel.class, "pressure_plate_inventory", stdOverlay, PaintRegistry.OVERLAY_TRANSFORMATION);
      list.add(model2);
    }
    return list;
  }

  @Override
  public boolean canRenderInLayer(@Nonnull IBlockState state, @Nonnull BlockRenderLayer layer) {
    return true;
  }

  @Override
  public int getFlammability(@Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nonnull EnumFacing face) {
    TileEntity te = BlockEnder.getAnyTileEntitySafe(world, pos);
    if (te instanceof TilePaintedPressurePlate) {
      return EnumPressurePlateType.WOOD == ((TilePaintedPressurePlate) te).getType() ? 20 : 0;
    }
    return 0;
  }

  @Override
  public int getFireSpreadSpeed(@Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nonnull EnumFacing face) {
    TileEntity te = BlockEnder.getAnyTileEntitySafe(world, pos);
    if (te instanceof TilePaintedPressurePlate) {
      return EnumPressurePlateType.WOOD == ((TilePaintedPressurePlate) te).getType() ? 5 : 0;
    }
    return 0;
  }

  @Override
  public void getSubBlocks(@Nonnull CreativeTabs tab, @Nonnull NonNullList<ItemStack> list) {
    for (EnumPressurePlateType type : EnumPressurePlateType.values()) {
      if (!type.isShadowsVanilla()) {
        list.add(new ItemStack(this, 1, EnumPressurePlateType.getMetaFromType(type, false)));
      }
      list.add(new ItemStack(this, 1, EnumPressurePlateType.getMetaFromType(type, true)));
    }
  }

  @Override
  @Nonnull
  public NNList<ItemStack> getSubItems() {
    NNList<ItemStack> list = new NNList<>();
    for (EnumPressurePlateType type : EnumPressurePlateType.values()) {
      list.add(new ItemStack(this, 1, EnumPressurePlateType.getMetaFromType(type, false)));
      list.add(new ItemStack(this, 1, EnumPressurePlateType.getMetaFromType(type, true)));
    }
    return list;
  }

  @Override
  protected void updateState(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state, int oldRedstoneStrength) {
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
          worldIn.playSound(null, pos.getX() + 0.5D, pos.getY() + 0.1D, pos.getZ() + 0.5D, SoundEvents.UI_BUTTON_CLICK, SoundCategory.BLOCKS, 0.3F, 0.5F);
        } else if (isOn && !wasOn) {
          worldIn.playSound(null, pos.getX() + 0.5D, pos.getY() + 0.1D, pos.getZ() + 0.5D, SoundEvents.UI_BUTTON_CLICK, SoundCategory.BLOCKS, 0.3F, 0.6F);
        }
      }
    }

    if (isOn) {
      worldIn.scheduleUpdate(new BlockPos(pos), this, this.tickRate(worldIn));
    }
  }

  public static class BlockItemPaintedPressurePlate extends BlockItemPaintedBlock implements IOverlayRenderAware {

    public BlockItemPaintedPressurePlate(@Nonnull BlockPaintedPressurePlate block) {
      super(block);
    }

    @Override
    public boolean hasEffect(@Nonnull ItemStack stack) {
      return EnumPressurePlateType.getTypeFromMeta(stack.getMetadata()) == EnumPressurePlateType.TUNED;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(@Nonnull ItemStack stack, @Nullable World worldIn, @Nonnull List<String> tooltip, @Nonnull ITooltipFlag flagIn) {
      super.addInformation(stack, worldIn, tooltip, flagIn);
      CapturedMob capturedMob = CapturedMob.create(stack);
      if (capturedMob != null) {
        tooltip.add(Lang.PRESSURE_PLATE_TUNED.get(capturedMob.getDisplayName()));
      }
    }

    @Override
    public void renderItemOverlayIntoGUI(@Nonnull ItemStack stack, int xPosition, int yPosition) {
      MobNameOverlayRenderHelper.doItemOverlayIntoGUI(stack, xPosition, yPosition);
    }

  }

  @Override
  public @Nonnull String getUnlocalizedName(int meta) {
    return getUnlocalizedName() + "." + EnumPressurePlateType.getTypeFromMeta(meta).getName()
        + (EnumPressurePlateType.getSilentFromMeta(meta) ? ".silent" : "");
  }

  @Override
  public @Nonnull String getUnlocalizedNameForTooltip(@Nonnull ItemStack itemStack) {
    return getUnlocalizedName(itemStack.getMetadata());
  }

  @Override
  @SideOnly(Side.CLIENT)
  public List<IBlockState> mapBlockRender(@Nonnull IBlockStateWrapper state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos,
      @Nullable BlockRenderLayer blockLayer, @Nonnull QuadCollector quadCollector) {
    IBlockState paintSource = getPaintSource(state, world, pos);
    if ((blockLayer == null || PaintUtil.canRenderInLayer(paintSource, blockLayer))
        && (paintSource == null || !(paintSource.getBlock() instanceof BlockFusedQuartzBase))) {
      quadCollector.addFriendlybakedModel(blockLayer, mapRender(state, paintSource, getRotation(world, pos)), paintSource, MathHelper.getPositionRandom(pos));
    }
    return null;
  }

  @Override
  protected void playClickOnSound(@Nonnull World worldIn, @Nonnull BlockPos color) {
    if (blockMaterial == Material.WOOD) {
      worldIn.playSound((EntityPlayer) null, color, SoundEvents.BLOCK_WOOD_PRESSPLATE_CLICK_ON, SoundCategory.BLOCKS, 0.3F, 0.8F);
    } else {
      worldIn.playSound((EntityPlayer) null, color, SoundEvents.BLOCK_STONE_PRESSPLATE_CLICK_ON, SoundCategory.BLOCKS, 0.3F, 0.6F);
    }

  }

  @Override
  protected void playClickOffSound(@Nonnull World worldIn, @Nonnull BlockPos pos) {
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
