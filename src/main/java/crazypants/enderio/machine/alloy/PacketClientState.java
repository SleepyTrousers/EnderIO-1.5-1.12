package crazypants.enderio.machine.alloy;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class PacketClientState implements IMessage, IMessageHandler<PacketClientState, IMessage> {

  private int x;
  private int y;
  private int z;

  private TileAlloySmelter.Mode mode;

  public PacketClientState() {

  }

  public PacketClientState(TileAlloySmelter tile) {
    x = tile.xCoord;
    y = tile.yCoord;
    z = tile.zCoord;
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

  public IMessage onMessage(PacketClientState message, MessageContext ctx) {
    handle(ctx.getServerHandler().playerEntity);
    return null;
  }

  private void handle(EntityPlayer player) {
    TileEntity te = player.worldObj.getTileEntity(x, y, z);
    if(te instanceof TileAlloySmelter) {
      TileAlloySmelter me = (TileAlloySmelter) te;
      me.setMode(mode);
      player.worldObj.markBlockForUpdate(x, y, z);
    }
  }

}
