package crazypants.enderio.machine.alloy;

import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketClientState implements IMessage, IMessageHandler<PacketClientState, IMessage> {

  private int x;
  private int y;
  private int z;

  private TileAlloySmelter.Mode mode;

  public PacketClientState() {

  }

  public PacketClientState(TileAlloySmelter tile) {
    x = tile.getPos().getX();
    y = tile.getPos().getY();
    z = tile.getPos().getZ();
    mode = tile.getMode();
  }

  @Override
  public void toBytes(ByteBuf buf) {
    buf.writeInt(x);
    buf.writeInt(y);
    buf.writeInt(z);
    buf.writeShort(mode.ordinal());
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    x = buf.readInt();
    y = buf.readInt();
    z = buf.readInt();
    short ordinal = buf.readShort();
    mode = TileAlloySmelter.Mode.values()[ordinal];

  }

  @Override
  public IMessage onMessage(PacketClientState message, MessageContext ctx) {
    TileEntity te = ctx.getServerHandler().playerEntity.worldObj.getTileEntity(new BlockPos(message.x, message.y, message.z));
    if(te instanceof TileAlloySmelter) {
      TileAlloySmelter me = (TileAlloySmelter) te;
      me.setMode(message.mode);
      ctx.getServerHandler().playerEntity.worldObj.markBlockForUpdate(new BlockPos(message.x, message.y, message.z));
    }
    return null;
  }
}
