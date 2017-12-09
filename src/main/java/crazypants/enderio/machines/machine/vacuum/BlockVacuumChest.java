package crazypants.enderio.machines.machine.vacuum;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.api.client.gui.IResourceTooltipProvider;

import crazypants.enderio.api.redstone.IRedstoneConnectable;
import crazypants.enderio.base.BlockEio;
import crazypants.enderio.base.gui.handler.IEioGuiHandler;
import crazypants.enderio.base.init.IModObject;
import crazypants.enderio.base.network.PacketHandler;
import crazypants.enderio.base.paint.IPaintable;
import crazypants.enderio.base.paint.PainterUtil2;
import crazypants.enderio.base.paint.render.PaintHelper;
import crazypants.enderio.base.render.IBlockStateWrapper;
import crazypants.enderio.base.render.IHaveRenderers;
import crazypants.enderio.base.render.IRenderMapper;
import crazypants.enderio.base.render.IRenderMapper.IItemRenderMapper;
import crazypants.enderio.base.render.ISmartRenderAwareBlock;
import crazypants.enderio.base.render.pipeline.BlockStateWrapperBase;
import crazypants.enderio.base.render.property.EnumRenderMode;
import crazypants.enderio.base.render.registry.SmartModelAttacher;
import crazypants.enderio.util.ClientUtil;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockVacuumChest extends BlockEio<TileVacuumChest> implements ISmartRenderAwareBlock, IEioGuiHandler.WithPos, IResourceTooltipProvider,
    IRedstoneConnectable, IPaintable.IBlockPaintableBlock, IPaintable.IWrenchHideablePaint, IHaveRenderers {

  public static BlockVacuumChest create(@Nonnull IModObject modObject) {
    PacketHandler.INSTANCE.registerMessage(PacketVaccumChest.Handler.class, PacketVaccumChest.class, PacketHandler.nextID(), Side.SERVER);
    BlockVacuumChest res = new BlockVacuumChest(modObject);
    res.init();
    return res;
  }

  protected BlockVacuumChest(@Nonnull IModObject modObject) {
    super(modObject, TileVacuumChest.class);
    initDefaultState();
  }

  protected void initDefaultState() {
    setDefaultState(this.blockState.getBaseState().withProperty(EnumRenderMode.RENDER, EnumRenderMode.AUTO));
  }

  @Override
  public boolean shouldRedstoneConduitConnect(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull EnumFacing from) {
    return true;
  }

  @Override
  public void neighborChanged(@Nonnull IBlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull Block neighborBlock,
      @Nonnull BlockPos fromPos) {
    TileVacuumChest ent = getTileEntity(world, pos);
    if (ent != null) {
      ent.onNeighborBlockChange(state, world, fromPos, neighborBlock, fromPos);
    }
  }

  @Override
  protected void init() {
    super.init();
    registerInSmartModelAttacher();
  }

  protected void registerInSmartModelAttacher() {
    SmartModelAttacher.register(this);
  }

  @Override
  protected @Nonnull BlockStateContainer createBlockState() {
    return new BlockStateContainer(this, new IProperty[] { EnumRenderMode.RENDER });
  }

  @Override
  public @Nonnull IBlockState getStateFromMeta(int meta) {
    return getDefaultState();
  }

  @Override
  public int getMetaFromState(@Nonnull IBlockState state) {
    return 0;
  }

  @Override
  public @Nonnull IBlockState getActualState(@Nonnull IBlockState state, @Nonnull IBlockAccess worldIn, @Nonnull BlockPos pos) {
    return getDefaultState();
  }

  @Override
  @SideOnly(Side.CLIENT)
  public final @Nonnull IBlockState getExtendedState(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos) {
    IBlockStateWrapper blockStateWrapper = createBlockStateWrapper(state, world, pos);
    TileVacuumChest tileEntity = getTileEntitySafe(world, pos);
    if (tileEntity != null) {
      setBlockStateWrapperCache(blockStateWrapper, world, pos, tileEntity);
    }
    blockStateWrapper.bakeModel();
    return blockStateWrapper;
  }

  protected void setBlockStateWrapperCache(@Nonnull IBlockStateWrapper blockStateWrapper, @Nonnull IBlockAccess world, @Nonnull BlockPos pos,
      @Nonnull TileVacuumChest tileEntity) {
    blockStateWrapper.addCacheKey(0);
  }

  protected @Nonnull BlockStateWrapperBase createBlockStateWrapper(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos) {
    return new BlockStateWrapperBase(state, world, pos, getBlockRenderMapper());
  }

  @Override
  @SideOnly(Side.CLIENT)
  public @Nonnull IItemRenderMapper getItemRenderMapper() {
    return VacuumRenderMapper.instance;
  }

  @SideOnly(Side.CLIENT)
  public IRenderMapper.IBlockRenderMapper getBlockRenderMapper() {
    return VacuumRenderMapper.instance;
  }

  @Override
  public boolean doNormalDrops(IBlockAccess world, BlockPos pos) {
    return false;
  }

  @Override
  protected void processDrop(@Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nullable TileVacuumChest te, @Nonnull ItemStack drop) {
    if (te != null) {
      te.writeToItemStack(drop);
    }
  }

  @Override
  public void onBlockPlacedBy(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull EntityLivingBase placer,
      @Nonnull ItemStack stack) {
    if (!world.isRemote) {
      TileEntity te = world.getTileEntity(pos);
      if (te instanceof TileVacuumChest) {
        ((TileVacuumChest) te).readFromItemStack(stack);
        world.notifyBlockUpdate(pos, state, state, 3);
      }
    }
  }

  @Override
  @SideOnly(Side.CLIENT)
  public @Nonnull BlockRenderLayer getBlockLayer() {
    return BlockRenderLayer.CUTOUT;
  }

  @Override
  public boolean isOpaqueCube(@Nonnull IBlockState blockStateIn) {
    return false;
  }

  @Override
  public @Nullable Container getServerGuiElement(@Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos, @Nullable EnumFacing facing,
      int param1) {
    TileVacuumChest te = getTileEntity(world, pos);
    if (te != null) {
      return new ContainerVacuumChest(player.inventory, te);
    }
    return null;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public @Nullable GuiScreen getClientGuiElement(@Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos, @Nullable EnumFacing facing,
      int param1) {
    TileVacuumChest te = getTileEntity(world, pos);
    if (te != null) {
      return new GuiVacuumChest(player.inventory, te);
    }
    return null;
  }

  @Override
  public @Nonnull String getUnlocalizedNameForTooltip(@Nonnull ItemStack itemStack) {
    return getUnlocalizedName();
  }

  // ///////////////////////////////////////////////////////////////////////
  // PAINT START
  // ///////////////////////////////////////////////////////////////////////

  @SuppressWarnings("null")
  @Override
  public @Nonnull IBlockState getFacade(@Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nullable EnumFacing side) {
    IBlockState paintSource = getPaintSource(getDefaultState(), world, pos);
    return paintSource != null ? paintSource : world.getBlockState(pos);
  }

  @Override
  public void setPaintSource(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nullable IBlockState paintSource) {
    TileVacuumChest te = getTileEntity(world, pos);
    if (te != null) {
      te.setPaintSource(paintSource);
    }
  }

  @Override
  public void setPaintSource(@Nonnull Block block, @Nonnull ItemStack stack, @Nullable IBlockState paintSource) {
    PainterUtil2.setSourceBlock(stack, paintSource);
  }

  @Override
  public IBlockState getPaintSource(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos) {
    TileVacuumChest te = getTileEntitySafe(world, pos);
    if (te != null) {
      return te.getPaintSource();
    }
    return null;
  }

  @Override
  public IBlockState getPaintSource(@Nonnull Block block, @Nonnull ItemStack stack) {
    return PainterUtil2.getSourceBlock(stack);
  }

  @Override
  public boolean canRenderInLayer(@Nonnull IBlockState state, @Nonnull BlockRenderLayer layer) {
    return true;
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

  // ///////////////////////////////////////////////////////////////////////
  // PAINT END
  // ///////////////////////////////////////////////////////////////////////

  @Override
  @SideOnly(Side.CLIENT)
  public void registerRenderers(@Nonnull IModObject modObject) {
    ClientUtil.registerDefaultItemRenderer(modObject);
  }

  @Override
  public boolean hasComparatorInputOverride(@Nonnull IBlockState state) {
    return true;
  }

  @Override
  public int getComparatorInputOverride(@Nonnull IBlockState blockState1, @Nonnull World worldIn, @Nonnull BlockPos pos) {
    return Container.calcRedstone(getTileEntity(worldIn, pos));
  }

}
