package crazypants.enderio.block.detector;

import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.BlockEnder;
import com.enderio.core.common.util.NNList;

import crazypants.enderio.BlockEio;
import crazypants.enderio.EnderIOTab;
import crazypants.enderio.block.painted.BlockItemPaintedBlock;
import crazypants.enderio.block.painted.TileEntityPaintedBlock;
import crazypants.enderio.init.IModObject;
import crazypants.enderio.paint.IPaintable;
import crazypants.enderio.paint.PainterUtil2;
import crazypants.enderio.paint.render.PaintHelper;
import crazypants.enderio.render.IBlockStateWrapper;
import crazypants.enderio.render.IHaveRenderers;
import crazypants.enderio.render.pipeline.BlockStateWrapperRelay;
import crazypants.enderio.render.registry.SmartModelAttacher;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.DefaultStateMapper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockDetector extends BlockEio<TileEntityPaintedBlock> implements IPaintable.ISolidBlockPaintableBlock, IHaveRenderers {

  public static final @Nonnull PropertyBool IS_ON = PropertyBool.create("on");
  public static final @Nonnull PropertyDirection FACING = PropertyDirection.create("facing");

  public static BlockDetector create(@Nonnull IModObject modObject) {
    BlockDetector result = new BlockDetector(modObject, false);
    result.init();
    return result;
  }

  public static BlockDetector createSilent(@Nonnull IModObject modObject) {
    BlockDetector result = new BlockDetector(modObject, true);
    result.init();
    return result;
  }

  private final boolean silent;

  protected BlockDetector(@Nonnull IModObject modObject, boolean silent) {
    super(modObject.getUnlocalisedName(), TileEntityPaintedBlock.class);
    this.silent = silent;
    setCreativeTab(EnderIOTab.tabEnderIOMachines);
    initDefaultState();
  }

  @Override
  protected void init() {
    super.init();
    SmartModelAttacher.registerNoProps(this);
  }

  protected void initDefaultState() {
    setDefaultState(this.blockState.getBaseState());
  }

  @Override
  protected @Nonnull BlockStateContainer createBlockState() {
    return new BlockStateContainer(this, new IProperty[] { IS_ON, FACING });
  }

  @Override
  public @Nonnull IBlockState getStateFromMeta(int meta) {
    return getDefaultState().withProperty(IS_ON, (meta & 0x08) != 0).withProperty(FACING, NNList.FACING.get(meta & 0x7));
  }

  @Override
  public int getMetaFromState(@Nonnull IBlockState state) {
    return (state.getValue(IS_ON) ? 8 : 0) + state.getValue(FACING).ordinal();
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void registerRenderers(@Nonnull IModObject modObject) {
    Item item = Item.getItemFromBlock(this);
    Map<IBlockState, ModelResourceLocation> locations = new DefaultStateMapper().putStateModelLocations(this);
    IBlockState state = getDefaultState().withProperty(IS_ON, true).withProperty(FACING, EnumFacing.UP);
    ModelResourceLocation mrl = locations.get(state);
    ModelLoader.setCustomModelResourceLocation(item, 0, mrl);
  }

  @Override
  public @Nonnull IBlockState withRotation(@Nonnull IBlockState state, @Nonnull Rotation rot) {
    return state.withProperty(FACING, rot.rotate(state.getValue(FACING)));
  }

  @Override
  public @Nonnull IBlockState withMirror(@Nonnull IBlockState state, @Nonnull Mirror mirrorIn) {
    return state.withRotation(mirrorIn.toRotation(state.getValue(FACING)));
  }

  @Override
  public @Nonnull IBlockState getExtendedState(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos) {
    IBlockStateWrapper blockStateWrapper = new BlockStateWrapperRelay(state, world, pos);
    blockStateWrapper.bakeModel();
    return blockStateWrapper;
  }

  @Override
  protected ItemBlock createItemBlock() {
    return new BlockItemPaintedBlock(this, getName());
  }

  @Override
  public boolean doNormalDrops(IBlockAccess world, BlockPos pos) {
    return false;
  }

  @Override
  protected void processDrop(@Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nullable TileEntityPaintedBlock te, @Nonnull ItemStack drop) {
    PainterUtil2.setSourceBlock(drop, getPaintSource(world.getBlockState(pos), world, pos));
  }

  @Override
  public int getWeakPower(@Nonnull IBlockState state, @Nonnull IBlockAccess blockAccess, @Nonnull BlockPos pos, @Nonnull EnumFacing side) {
    return side.getOpposite() != state.getValue(FACING) && state.getValue(IS_ON) ? 15 : 0;
  }

  @Override
  public boolean canProvidePower(@Nonnull IBlockState state) {
    return true;
  }

  protected void playClickSound(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
    if (state.getValue(IS_ON)) {
      worldIn.playSound((EntityPlayer) null, pos, SoundEvents.BLOCK_STONE_PRESSPLATE_CLICK_ON, SoundCategory.BLOCKS, 0.3F, 0.6F);
    } else {
      worldIn.playSound((EntityPlayer) null, pos, SoundEvents.BLOCK_STONE_PRESSPLATE_CLICK_OFF, SoundCategory.BLOCKS, 0.3F, 0.5F);
    }
  }

  @Override
  public void neighborChanged(@Nonnull IBlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull Block blockIn, @Nonnull BlockPos fromPos) {
    IBlockState newState = state.withProperty(IS_ON, isTargetBlockAir(state, world, pos));
    if (newState != state) {
      world.setBlockState(pos, newState);
      if (!silent) {
        playClickSound(world, pos, newState);
      }
    }
  }

  protected boolean isTargetBlockAir(IBlockState state, World world, BlockPos pos) {
    return world.isAirBlock(pos.offset(state.getValue(FACING)));
  }

  @Override
  public @Nonnull IBlockState getStateForPlacement(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull EnumFacing facing, float hitX, float hitY, float hitZ,
      int meta, @Nonnull EntityLivingBase placer, @Nonnull EnumHand hand) {
    final IBlockState state = super.getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, meta, placer, hand).withProperty(FACING, facing);
    return state.withProperty(IS_ON, isTargetBlockAir(state, world, pos));
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
  public @Nonnull ItemStack getPickBlock(@Nonnull IBlockState state, @Nonnull RayTraceResult target, @Nonnull World world, @Nonnull BlockPos pos,
      @Nonnull EntityPlayer player) {
    final ItemStack pickBlock = super.getPickBlock(state, target, world, pos, player);
    PainterUtil2.setSourceBlock(pickBlock, getPaintSource(world.getBlockState(pos), world, pos));
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

  @SuppressWarnings("null")
  @Override
  public @Nonnull IBlockState getFacade(@Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nullable EnumFacing side) {
    IBlockState paintSource = getPaintSource(getDefaultState(), world, pos);
    return paintSource != null ? paintSource : world.getBlockState(pos);
  }

  @Override
  public @Nonnull BlockRenderLayer getBlockLayer() {
    return BlockRenderLayer.SOLID;
  }

  @Override
  public boolean canRenderInLayer(@Nonnull IBlockState state, @Nonnull BlockRenderLayer layer) {
    return true;
  }

  @Override
  public boolean isSideSolid(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nonnull EnumFacing side) {
    return state.getValue(FACING) == side.getOpposite();
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
