package crazypants.enderio.machines.machine.ihopper;

import java.util.Random;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import crazypants.enderio.base.BlockEio;
import crazypants.enderio.base.gui.handler.IEioGuiHandler;
import crazypants.enderio.base.init.IModObject;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockImpulseHopper extends BlockEio<TileImpulseHopper> implements IEioGuiHandler.WithPos, ITileEntityProvider {

  public static BlockImpulseHopper create(@Nonnull IModObject modObject) {
    BlockImpulseHopper iHopper = new BlockImpulseHopper(modObject);
    iHopper.init();
    return iHopper;
  }

  protected BlockImpulseHopper(@Nonnull IModObject mo) {
    super(mo);
  }

  @Override
  @Nullable
  public TileEntity createNewTileEntity(World worldIn, int meta) {
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

  @SideOnly(Side.CLIENT)
  @Override
  public void randomDisplayTick(@Nonnull IBlockState stateIn, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull Random rand) {
    if (getTileEntity(world, pos).isActive() && world.getBlockState(pos.up()).isOpaqueCube()) {
      if (rand.nextInt(8) == 0) {
        float startX = pos.getX() + 0.8F - rand.nextFloat() * 0.6F;
        float startY = pos.getY() + 1.0F;
        float startZ = pos.getZ() + 0.8F - rand.nextFloat() * 0.6F;
        world.spawnParticle(EnumParticleTypes.REDSTONE, startX, startY, startZ, 0.0D, -0.2D, 0.0D);
      }
    }
    super.randomDisplayTick(stateIn, world, pos, rand);
  }

}
