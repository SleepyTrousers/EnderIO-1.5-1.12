package crazypants.enderio.machines.machine.generator.stirling;

import java.util.Random;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import crazypants.enderio.base.GuiID;
import crazypants.enderio.base.gui.handler.IEioGuiHandler;
import crazypants.enderio.base.init.IModObject;
import crazypants.enderio.base.machine.base.block.AbstractMachineBlock;
import crazypants.enderio.base.paint.IPaintable;
import crazypants.enderio.base.render.IBlockStateWrapper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockStirlingGenerator<T extends TileStirlingGenerator> extends AbstractMachineBlock<T>
    implements IPaintable.ISolidBlockPaintableBlock, IPaintable.IWrenchHideablePaint, IEioGuiHandler.WithPos {

  public static BlockStirlingGenerator<TileStirlingGenerator> create(@Nonnull IModObject modObject) {
    BlockStirlingGenerator<TileStirlingGenerator> gen = new BlockStirlingGenerator<>(modObject, TileStirlingGenerator.class);
    gen.init();
    return gen;
  }

  public static BlockStirlingGenerator<TileStirlingGenerator.Simple> create_simple(@Nonnull IModObject modObject) {
    BlockStirlingGenerator<TileStirlingGenerator.Simple> gen = new BlockStirlingGenerator<>(modObject, TileStirlingGenerator.Simple.class);
    gen.init();
    return gen;
  }

  protected BlockStirlingGenerator(@Nonnull IModObject modObject, @Nonnull Class<T> teClass) {
    super(modObject, teClass);
  }

  @Override
  @Nullable
  public Object getServerGuiElement(@Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos, @Nullable EnumFacing facing,
      int param1) {
    T te = getTileEntity(world, pos);
    if (te != null) {
      return new ContainerStirlingGenerator<T>(player.inventory, te);
    }
    return null;
  }

  @Override
  @Nullable
  public Object getClientGuiElement(@Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos, @Nullable EnumFacing facing,
      int param1) {
    T te = getTileEntity(world, pos);
    if (te != null) {
      return new GuiStirlingGenerator<T>(player.inventory, te);
    }
    return null;
  }

  @Override
  public Object getServerGuiElement(int ID, @Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos) {
    return null;
  }

  @Override
  public Object getClientGuiElement(int ID, @Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos) {
    return null;
  }

  @Override
  protected @Nonnull GuiID getGuiId() {
    return null;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void randomDisplayTick(@Nonnull IBlockState bs, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull Random rand) {
    T te = getTileEntity(world, pos);
    if (te != null && te.isActive()) {
      EnumFacing front = te.facing;
      for (int i = 0; i < 2; i++) {
        double px = pos.getX() + 0.5 + front.getFrontOffsetX() * 0.6;
        double pz = pos.getZ() + 0.5 + front.getFrontOffsetZ() * 0.6;
        double v = 0.05;
        double vx = 0;
        double vz = 0;

        if (front == EnumFacing.NORTH || front == EnumFacing.SOUTH) {
          px += world.rand.nextFloat() * 0.9 - 0.45;
          vz += front == EnumFacing.NORTH ? -v : v;
        } else {
          pz += world.rand.nextFloat() * 0.9 - 0.45;
          vx += front == EnumFacing.WEST ? -v : v;
        }

        if (te.isLavaFired && rand.nextInt(40) == 0) {
          world.spawnParticle(EnumParticleTypes.LAVA, px, pos.getY() + 0.1, pz, 0, 0, 0);
        }
        world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, px, pos.getY() + 0.1, pz, vx, 0, vz);
      }
    }
  }

  @Override
  protected void setBlockStateWrapperCache(@Nonnull IBlockStateWrapper blockStateWrapper, @Nonnull IBlockAccess world, @Nonnull BlockPos pos,
      @Nonnull T tileEntity) {
    blockStateWrapper.addCacheKey(tileEntity.getFacing()).addCacheKey(tileEntity.isActive());
  }

}
