package crazypants.enderio.machines.machine.mapomatic;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import crazypants.enderio.base.init.IModObject;
import crazypants.enderio.base.machine.base.block.AbstractCapabilityPoweredMachineBlock;
import crazypants.enderio.base.render.IBlockStateWrapper;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockMapOMatic extends AbstractCapabilityPoweredMachineBlock<TileMapOMatic> {

  @Nonnull
  public static BlockMapOMatic create(@Nonnull IModObject modObject) {
    return new BlockMapOMatic(modObject);
  }

  public BlockMapOMatic(@Nonnull IModObject mo) {
    super(mo);
    setShape(mkShape(BlockFaceShape.SOLID));
  }

  @Override
  protected void setBlockStateWrapperCache(@Nonnull IBlockStateWrapper blockStateWrapper, @Nonnull IBlockAccess world, @Nonnull BlockPos pos,
      @Nonnull TileMapOMatic tileEntity) {

  }

  @Nullable
  @Override
  public Container getServerGuiElement(@Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos, @Nullable EnumFacing facing, int param1,
      @Nonnull TileMapOMatic te) {
    return new ContainerMapOMatic(player.inventory, te);
  }

  @Nullable
  @Override
  public GuiScreen getClientGuiElement(@Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos, @Nullable EnumFacing facing, int param1,
      @Nonnull TileMapOMatic te) {
    return new GuiMapOMatic(player.inventory, te);
  }
}
