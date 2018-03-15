package crazypants.enderio.machines.machine.ihopper;

import java.util.Random;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.api.client.gui.IResourceTooltipProvider;

import crazypants.enderio.api.redstone_dont_crash_us_mcjty.IRedstoneConnectable_dont_crash_us_mcjty;
import crazypants.enderio.base.init.IModObject;
import crazypants.enderio.base.machine.base.block.AbstractMachineBlock;
import crazypants.enderio.base.render.IBlockStateWrapper;
import crazypants.enderio.base.render.IHaveRenderers;
import crazypants.enderio.base.render.IRenderMapper;
import crazypants.enderio.base.render.IRenderMapper.IItemRenderMapper;
import crazypants.enderio.base.render.ISmartRenderAwareBlock;
import crazypants.enderio.base.render.property.EnumRenderMode;
import crazypants.enderio.base.render.registry.SmartModelAttacher;
import crazypants.enderio.util.ClientUtil;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockImpulseHopper extends AbstractMachineBlock<TileImpulseHopper>
    implements IHaveRenderers, ISmartRenderAwareBlock, IRedstoneConnectable_dont_crash_us_mcjty, IResourceTooltipProvider {

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
  @SideOnly(Side.CLIENT)
  public @Nonnull BlockRenderLayer getBlockLayer() {
    return BlockRenderLayer.CUTOUT;
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
  protected void setBlockStateWrapperCache(@Nonnull IBlockStateWrapper blockStateWrapper, @Nonnull IBlockAccess world, @Nonnull BlockPos pos,
      @Nonnull TileImpulseHopper tileEntity) {
    blockStateWrapper.addCacheKey(tileEntity.isActive());
  }

  @Override
  @SideOnly(Side.CLIENT)
  public @Nonnull IItemRenderMapper getItemRenderMapper() {
    return ImpulseRenderMapper.instance;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public IRenderMapper.IBlockRenderMapper getBlockRenderMapper() {
    return ImpulseRenderMapper.instance;
  }

  @Override
  public @Nullable Container getServerGuiElement(@Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos, @Nullable EnumFacing facing,
      int param1, @Nonnull TileImpulseHopper te) {
    return new ContainerImpulseHopper(player.inventory, te);
  }

  @Override
  public @Nullable GuiScreen getClientGuiElement(@Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos, @Nullable EnumFacing facing,
      int param1, @Nonnull TileImpulseHopper te) {
    return new GuiImpulseHopper(player.inventory, te);
  }

  @SideOnly(Side.CLIENT)
  @Override
  public void randomDisplayTick(@Nonnull IBlockState stateIn, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull Random rand) {
    TileImpulseHopper te = getTileEntity(world, pos);

    if (te != null && te.isActive() && !world.getBlockState(pos.up()).isOpaqueCube()) {
      if (rand.nextInt(8) == 0) {
        float startX = pos.getX() + 0.8F - rand.nextFloat() * 0.6F;
        float startY = pos.getY() + 1.0F;
        float startZ = pos.getZ() + 0.8F - rand.nextFloat() * 0.6F;
        world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, startX, startY, startZ, 0.0D, -0.2D, 0.0D);
      }
      super.randomDisplayTick(stateIn, world, pos, rand);
    }
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void registerRenderers(@Nonnull IModObject mo) {
    ClientUtil.registerDefaultItemRenderer(mo);
  }

  @Override
  public boolean shouldRedstoneConduitConnect(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull EnumFacing from) {
    return true;
  }

  @Override
  public void neighborChanged(@Nonnull IBlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull Block neighborBlock,
      @Nonnull BlockPos fromPos) {
    super.neighborChanged(state, world, pos, neighborBlock, fromPos);
    TileImpulseHopper te = getTileEntity(world, pos);
    if (te != null) {
      te.onNeighborBlockChange(state, world, fromPos, neighborBlock, fromPos);
    }
  }

  @Override
  @Nonnull
  public String getUnlocalizedNameForTooltip(@Nonnull ItemStack itemStack) {
    return getUnlocalizedName();
  }

}
