package crazypants.enderio.machine.crusher;

import java.util.Random;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import crazypants.enderio.GuiHandler;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.AbstractMachineBlock;
import crazypants.enderio.machine.AbstractMachineEntity;
import crazypants.enderio.network.PacketHandler;
import crazypants.enderio.paint.IPaintable;
import crazypants.enderio.render.BlockStateWrapper;

public class BlockCrusher extends AbstractMachineBlock<TileCrusher> implements IPaintable.ISolidBlockPaintableBlock, IPaintable.IWrenchHideablePaint {

  public static BlockCrusher create() {
    PacketHandler.INSTANCE.registerMessage(PacketGrindingBall.class, PacketGrindingBall.class, PacketHandler.nextID(), Side.CLIENT);

    BlockCrusher res = new BlockCrusher();
    res.init();
    return res;
  }

  private BlockCrusher() {
    super(ModObject.blockSagMill, TileCrusher.class);
  }

  @Override
  public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    // The server needs the container as it manages the adding and removing of
    // items, which are then sent to the client for display
    TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
    if(te instanceof TileCrusher) {
      return new ContainerCrusher(player.inventory, (TileCrusher) te);
    }
    return null;
  }

  @Override
  public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
    if(te instanceof TileCrusher) {
      return new GuiCrusher(player.inventory, (TileCrusher) te);
    }
    return null;
  }

  @Override
  protected int getGuiId() {
    return GuiHandler.GUI_ID_CRUSHER;
  }

  @Override
  public void randomDisplayTick(World world, BlockPos pos, IBlockState state, Random rand) {
  
    TileCrusher te = (TileCrusher) world.getTileEntity(pos);
    if(te != null && te.isActive()) {
      EnumFacing front = te.facing;

      for (int i = 0; i < 3; i++) {
        double px = pos.getX() + 0.5 + front.getFrontOffsetX() * 0.51;
        double pz = pos.getZ() + 0.5 + front.getFrontOffsetZ() * 0.51;
        double py = pos.getY() + world.rand.nextFloat() * 0.8f + 0.1f;
        double v = 0.05;
        double vx = 0;
        double vz = 0;

        if(front == EnumFacing.NORTH || front == EnumFacing.SOUTH) {
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
  public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
    BlockStateWrapper extendedState = (BlockStateWrapper) super.getExtendedState(state, world, pos);
    TileEntity tileEntity = extendedState.getTileEntity();
    if (tileEntity instanceof AbstractMachineEntity) {
      extendedState.setCacheKey(((AbstractMachineEntity) tileEntity).getFacing(), ((AbstractMachineEntity) tileEntity).isActive());
    }
    return extendedState;
  }

}
