package crazypants.enderio.invpanel.sensor;

import java.util.Random;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.api.client.gui.IResourceTooltipProvider;

import crazypants.enderio.api.IModObject;
import crazypants.enderio.base.EnderIOTab;
import crazypants.enderio.base.machine.base.block.AbstractMachineBlock;
import crazypants.enderio.base.machine.modes.IoMode;
import crazypants.enderio.base.paint.IPaintable;
import crazypants.enderio.base.render.IBlockStateWrapper;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockInventoryPanelSensor extends AbstractMachineBlock<TileInventoryPanelSensor>
    implements IResourceTooltipProvider, IPaintable.ISolidBlockPaintableBlock, IPaintable.IWrenchHideablePaint {

  public static BlockInventoryPanelSensor create(@Nonnull IModObject modObject) {
    BlockInventoryPanelSensor result = new BlockInventoryPanelSensor(modObject);
    result.init();
    return result;
  }

  public BlockInventoryPanelSensor(@Nonnull IModObject modObject) {
    super(modObject);
    setCreativeTab(EnderIOTab.tabEnderIOInvpanel);
    setShape(mkShape(BlockFaceShape.SOLID));
  }

  @Override
  @Nullable
  public Container getServerGuiElement(@Nonnull EntityPlayer player, @Nonnull World world, @Nullable BlockPos pos, @Nullable EnumFacing facing, int param1,
      @Nonnull TileInventoryPanelSensor te) {
    return new ContainerSensor(player.inventory, te);
  }

  @Override
  @SideOnly(Side.CLIENT)
  @Nullable
  public GuiScreen getClientGuiElement(@Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos, @Nullable EnumFacing facing, int param1,
      @Nonnull TileInventoryPanelSensor te) {
    return new GuiSensor(player.inventory, te);
  }

  @Override
  public boolean isOpaqueCube(@Nonnull IBlockState bs) {
    return false;
  }

  @Override
  protected void setBlockStateWrapperCache(@Nonnull IBlockStateWrapper blockStateWrapper, @Nonnull IBlockAccess world, @Nonnull BlockPos pos,
      @Nonnull TileInventoryPanelSensor tileEntity) {
    blockStateWrapper.addCacheKey(tileEntity.getFacing());
    blockStateWrapper.addCacheKey(tileEntity.isActive());
  }

  @SideOnly(Side.CLIENT)
  @Override
  public void randomDisplayTick(@Nonnull IBlockState bs, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull Random rand) {
  }

  @Deprecated
  @Override
  public int getWeakPower(@Nonnull IBlockState blockStateIn, @Nonnull IBlockAccess blockAccess, @Nonnull BlockPos pos, @Nonnull EnumFacing side) {
    TileInventoryPanelSensor te = getTileEntity(blockAccess, pos);
    if (te != null) {
      int res = te.getIoMode(side.getOpposite()) != IoMode.DISABLED ? te.getRedstoneLevel() : 0;
      return res;
    }
    return super.getWeakPower(blockStateIn, blockAccess, pos, side);
  }

  @Override
  public boolean canProvidePower(@Nonnull IBlockState state) {
    return true;
  }

  @Override
  public boolean canConnectRedstone(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nonnull EnumFacing side) {
    TileInventoryPanelSensor te = getTileEntitySafe(world, pos);
    if (te != null && side != null) {
      return te.getIoMode(side.getOpposite()) != IoMode.DISABLED;
    }
    return super.canConnectRedstone(state, world, pos, side);
  }
}
