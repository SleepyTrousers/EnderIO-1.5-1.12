package crazypants.enderio.conduit.packet;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import crazypants.enderio.conduit.ConnectionMode;
import crazypants.enderio.conduit.IConduit;

public class PacketConnectionMode extends AbstractConduitPacket<IConduit> implements IMessageHandler<PacketConnectionMode, IMessage>{

  private ForgeDirection dir;
  private ConnectionMode mode;

  public PacketConnectionMode() {
  }

  public PacketConnectionMode(IConduit con, ForgeDirection dir) {
    super(con.getBundle().getEntity(), ConTypeEnum.get(con));
    this.dir = dir;
    mode = con.getConectionMode(dir);
  }

  @Override
  public void toBytes(ByteBuf buf) {
    super.toBytes(buf);
    buf.writeShort(dir.ordinal());
    buf.writeShort(mode.ordinal());
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    super.fromBytes(buf);
    dir = ForgeDirection.values()[buf.readShort()];
    mode = ConnectionMode.values()[buf.readShort()];

  }

  @Override
  public IMessage onMessage(PacketConnectionMode message, MessageContext ctx) {
    message.getTileCasted(ctx).setConnectionMode(message.dir, message.mode);
    message.getWorld(ctx).markBlockForUpdate(message.x, message.y, message.z);
    return null;
  }

}
