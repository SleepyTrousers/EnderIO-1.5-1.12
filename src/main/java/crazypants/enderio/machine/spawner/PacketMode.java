package crazypants.enderio.machine.spawner;

import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketMode implements IMessage, IMessageHandler<PacketMode, IMessage> {

  private int x;
  private int y;
  private int z;

  private boolean isSpawnMode;

  public PacketMode() {

  }

  public PacketMode(TilePoweredSpawner tile) {
    BlockPos p = tile.getPos();
    x = p.getX();
    y = p.getY();
    z = p.getZ();
    isSpawnMode = tile.isSpawnMode();
  }

  @Override
  public void toBytes(ByteBuf buf) {
    buf.writeInt(x);
    buf.writeInt(y);
    buf.writeInt(z);
    buf.writeBoolean(isSpawnMode);
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    x = buf.readInt();
    y = buf.readInt();
    z = buf.readInt();
    isSpawnMode = buf.readBoolean();

  }

  @Override
  public IMessage onMessage(PacketMode message, MessageContext ctx) {
    TileEntity te = ctx.getServerHandler().playerEntity.world.getTileEntity(new BlockPos(message.x, message.y, message.z));
    if(te instanceof TilePoweredSpawner) {
      TilePoweredSpawner me = (TilePoweredSpawner) te;
      me.setSpawnMode(message.isSpawnMode);
    }
    return null;
  }
}
