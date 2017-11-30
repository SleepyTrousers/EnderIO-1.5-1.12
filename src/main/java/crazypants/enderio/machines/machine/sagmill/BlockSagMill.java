package crazypants.enderio.machines.machine.sagmill;

import java.util.Random;

import javax.annotation.Nonnull;

import crazypants.enderio.base.GuiID;
import crazypants.enderio.base.init.IModObject;
import crazypants.enderio.base.machine.base.block.AbstractMachineBlock;
import crazypants.enderio.base.network.PacketHandler;
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

public class BlockSagMill extends AbstractMachineBlock<TileSagMill> implements IPaintable.ISolidBlockPaintableBlock, IPaintable.IWrenchHideablePaint {

  public static BlockSagMill create(@Nonnull IModObject modObject) {
    PacketHandler.INSTANCE.registerMessage(PacketGrindingBall.class, PacketGrindingBall.class, PacketHandler.nextID(), Side.CLIENT);

    BlockSagMill res = new BlockSagMill(modObject);
    res.init();
    return res;
  }

  private BlockSagMill(@Nonnull IModObject modObject) {
    super(modObject, TileSagMill.class);
  }

  @Override
  public Object getServerGuiElement(int ID, @Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos) {
    TileSagMill te = getTileEntity(world, pos);
    if (te != null) {
      return new ContainerSagMill(player.inventory, te);
    }
    return null;
  }

  @Override
  public Object getClientGuiElement(int ID, @Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos) {
    TileSagMill te = getTileEntity(world, pos);
    if (te != null) {
      return new GuiSagMill(player.inventory, te);
    }
    return null;
  }

  @Override
  protected @Nonnull GuiID getGuiId() {
    return GuiID.GUI_ID_CRUSHER;
  }

  @Override
  public void randomDisplayTick(@Nonnull IBlockState bs, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull Random rand) {

    TileSagMill te = (TileSagMill) world.getTileEntity(pos);
    if (te != null && te.isActive()) {
      EnumFacing front = te.facing;

      for (int i = 0; i < 3; i++) {
        double px = pos.getX() + 0.5 + front.getFrontOffsetX() * 0.51;
        double pz = pos.getZ() + 0.5 + front.getFrontOffsetZ() * 0.51;
        double py = pos.getY() + world.rand.nextFloat() * 0.8f + 0.1f;
        double v = 0.05;
        double vx = 0;
        double vz = 0;

        if (front == EnumFacing.NORTH || front == EnumFacing.SOUTH) {
          px += world.rand.nextFloat() * 0.8 - 0.4;
          vz += front == EnumFacing.NORTH ? -v : v;
        } else {
          pz += world.rand.nextFloat() * 0.8 - 0.4;
          vx += front == EnumFacing.WEST ? -v : v;
        }

        world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, px, py, pz, vx, 0, vz);
      }
    }
  }

  @Override
  protected void setBlockStateWrapperCache(@Nonnull IBlockStateWrapper blockStateWrapper, @Nonnull IBlockAccess world, @Nonnull BlockPos pos,
      @Nonnull TileSagMill tileEntity) {
    blockStateWrapper.addCacheKey(tileEntity.getFacing()).addCacheKey(tileEntity.isActive());
  }

}
