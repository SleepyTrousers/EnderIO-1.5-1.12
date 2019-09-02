package crazypants.enderio.machines.machine.niard;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.BlockEnder;
import com.enderio.core.common.util.NNList;

import crazypants.enderio.api.IModObject;
import crazypants.enderio.base.machine.base.block.AbstractCapabilityMachineBlock;
import crazypants.enderio.base.machine.render.RenderMappers;
import crazypants.enderio.base.paint.IPaintable;
import crazypants.enderio.base.render.IBlockStateWrapper;
import crazypants.enderio.base.render.ICustomSubItems;
import crazypants.enderio.base.render.IHaveTESR;
import crazypants.enderio.base.render.IRenderMapper;
import crazypants.enderio.base.render.IRenderMapper.IItemRenderMapper;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockNiard extends AbstractCapabilityMachineBlock<TileNiard>
    implements IPaintable.INonSolidBlockPaintableBlock, IPaintable.IWrenchHideablePaint, IHaveTESR, ICustomSubItems {

  public static BlockNiard create(@Nonnull IModObject modObject) {
    BlockNiard res = new BlockNiard(modObject);
    res.init();
    return res;
  }

  protected BlockNiard(@Nonnull IModObject modObject) {
    super(modObject);
    setLightOpacity(0);
    setShape(mkShape(BlockFaceShape.SOLID, BlockFaceShape.UNDEFINED, BlockFaceShape.SOLID));
  }

  @Override
  public @Nonnull TileNiard createTileEntity(@Nonnull World world, @Nonnull IBlockState state) {
    return new TileNiard();
  }

  @Override
  public @Nullable Container getServerGuiElement(@Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos, @Nullable EnumFacing facing,
      int param1, @Nonnull TileNiard te) {
    return null; // new ContainerTank(player.inventory, te);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public @Nullable GuiScreen getClientGuiElement(@Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos, @Nullable EnumFacing facing,
      int param1, @Nonnull TileNiard te) {
    return null; // new GuiTank(player.inventory, te);
  }

  @Override
  public boolean isOpaqueCube(@Nonnull IBlockState bs) {
    return false;
  }

  @Override
  public int getLightValue(@Nonnull IBlockState bs, @Nonnull IBlockAccess world, @Nonnull BlockPos pos) {
    TileNiard tank = BlockEnder.getAnyTileEntitySafe(world, pos, TileNiard.class);
    if (tank != null) {
      FluidStack stack = tank.getInputTank().getFluid();
      return stack == null || stack.amount <= 0 ? 0 : stack.getFluid().getLuminosity(stack);
    }
    return super.getLightValue(bs, world, pos);
  }

  @Override
  public boolean hasComparatorInputOverride(@Nonnull IBlockState bs) {
    return true;
  }

  @Override
  public int getComparatorInputOverride(@Nonnull IBlockState bs, @Nonnull World w, @Nonnull BlockPos pos) {
    TileNiard te = getTileEntity(w, pos);
    if (te != null) {
      return te.getComparatorOutput();
    }
    return 0;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public @Nonnull IItemRenderMapper getItemRenderMapper() {
    return RenderMappers.FRONT_MAPPER;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public IRenderMapper.IBlockRenderMapper getBlockRenderMapper() {
    return RenderMappers.FRONT_MAPPER;
  }

  @Override
  protected void setBlockStateWrapperCache(@Nonnull IBlockStateWrapper blockStateWrapper, @Nonnull IBlockAccess world, @Nonnull BlockPos pos,
      @Nonnull TileNiard tileEntity) {
    blockStateWrapper.addCacheKey(tileEntity.getFacing()).addCacheKey(tileEntity.isActive());
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void bindTileEntitySpecialRenderer() {
    ClientRegistry.bindTileEntitySpecialRenderer(TileNiard.class, new NiardFluidRenderer());
  }

  @Override
  @Nonnull
  public NNList<ItemStack> getSubItems() {
    return getSubItems(this, 0);
  }

}
