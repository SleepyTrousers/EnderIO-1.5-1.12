package crazypants.enderio.machines.machine.ihopper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import crazypants.enderio.base.BlockEio;
import crazypants.enderio.base.gui.handler.IEioGuiHandler;
import crazypants.enderio.base.init.IModObject;
import crazypants.enderio.base.render.IBlockStateWrapper;
import crazypants.enderio.base.render.IHaveRenderers;
import crazypants.enderio.base.render.IRenderMapper;
import crazypants.enderio.base.render.IRenderMapper.IItemRenderMapper;
import crazypants.enderio.base.render.ISmartRenderAwareBlock;
import crazypants.enderio.base.render.pipeline.BlockStateWrapperBase;
import crazypants.enderio.base.render.property.EnumRenderMode;
import crazypants.enderio.base.render.registry.SmartModelAttacher;
import crazypants.enderio.util.ClientUtil;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockImpulseHopper extends BlockEio<TileImpulseHopper>
    implements IEioGuiHandler.WithPos, ITileEntityProvider, IHaveRenderers, ISmartRenderAwareBlock {

  public static BlockImpulseHopper create(@Nonnull IModObject modObject) {
    BlockImpulseHopper iHopper = new BlockImpulseHopper(modObject);
    iHopper.init();
    return iHopper;
  }

  public BlockImpulseHopper(@Nonnull IModObject mo) {
    super(mo);
    setDefaultState(this.blockState.getBaseState().withProperty(EnumRenderMode.RENDER, EnumRenderMode.AUTO));
    setShape(mkShape(BlockFaceShape.SOLID));
  }

  @Override
  protected void init() {
    SmartModelAttacher.register(this);
  }

  @Override
  @Nonnull
  protected BlockStateContainer createBlockState() {
    return new BlockStateContainer(this, new IProperty[] { EnumRenderMode.RENDER });
  }

  @Override
  @Nonnull
  public IBlockState getStateFromMeta(int meta) {
    return getDefaultState();
  }

  @Override
  public int getMetaFromState(@Nonnull IBlockState state) {
    return 0;
  }

  @Override
  @Nonnull
  public IBlockState getActualState(@Nonnull IBlockState state, @Nonnull IBlockAccess worldIn, @Nonnull BlockPos pos) {
    return getDefaultState();
  }

  @Override
  @SideOnly(Side.CLIENT)
  @Nonnull
  public final IBlockState getExtendedState(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos) {
    IBlockStateWrapper blockStateWrapper = createBlockStateWrapper(state, world, pos);
    TileImpulseHopper tileEntity = getTileEntitySafe(world, pos);
    if (tileEntity != null) {
      setBlockStateWrapperCache(blockStateWrapper, world, pos, tileEntity);
    }
    blockStateWrapper.bakeModel();
    return blockStateWrapper;
  }

  protected void setBlockStateWrapperCache(@Nonnull IBlockStateWrapper blockStateWrapper, @Nonnull IBlockAccess world, @Nonnull BlockPos pos,
      @Nonnull TileImpulseHopper tileEntity) {
    blockStateWrapper.addCacheKey(tileEntity.isActive());
  }

  private @Nonnull BlockStateWrapperBase createBlockStateWrapper(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos) {
    return new BlockStateWrapperBase(state, world, pos, getBlockRenderMapper());
  }

  @Override
  @SideOnly(Side.CLIENT)
  public @Nonnull IItemRenderMapper getItemRenderMapper() {
    return ImpulseRenderMapper.instance;
  }

  @SideOnly(Side.CLIENT)
  public IRenderMapper.IBlockRenderMapper getBlockRenderMapper() {
    return ImpulseRenderMapper.instance;
  }

  @Override
  public boolean isOpaqueCube(@Nonnull IBlockState state) {
    return false;
  }

  @Override
  @Nullable
  public TileEntity createNewTileEntity(@Nonnull World worldIn, int meta) {
    return new TileImpulseHopper();
  }

  @Override
  @Nullable
  public Container getServerGuiElement(@Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos, @Nullable EnumFacing facing, int param1) {
    return new ContainerImpulseHopper(player.inventory, getTileEntity(world, pos));
  }

  @Override
  @Nullable
  public GuiScreen getClientGuiElement(@Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos, @Nullable EnumFacing facing, int param1) {
    return new GuiImpulseHopper(player.inventory, getTileEntity(world, pos));
  }

  // @SideOnly(Side.CLIENT)
  // @Override
  // public void randomDisplayTick(@Nonnull IBlockState stateIn, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull Random rand) {
  // if (getTileEntity(world, pos).isActive() && world.getBlockState(pos.up()).isOpaqueCube()) {
  // if (rand.nextInt(8) == 0) {
  // float startX = pos.getX() + 0.8F - rand.nextFloat() * 0.6F;
  // float startY = pos.getY() + 1.0F;
  // float startZ = pos.getZ() + 0.8F - rand.nextFloat() * 0.6F;
  // world.spawnParticle(EnumParticleTypes.REDSTONE, startX, startY, startZ, 0.0D, -0.2D, 0.0D);
  // }
  // }
  // super.randomDisplayTick(stateIn, world, pos, rand);
  // }

  @Override
  @SideOnly(Side.CLIENT)
  public void registerRenderers(@Nonnull IModObject mo) {
    ClientUtil.registerDefaultItemRenderer(mo);
  }

}
