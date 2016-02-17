package crazypants.enderio.teleport.packet;

import crazypants.enderio.EnderIO;
import crazypants.enderio.GuiHandler;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketOpenAuthGui implements IMessage, IMessageHandler<PacketOpenAuthGui, IMessage> {

  int x;
  int y;
  int z;

  public PacketOpenAuthGui() {

  }

  public PacketOpenAuthGui(int x, int y, int z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }

  @Override
  public void toBytes(ByteBuf buf) {
    buf.writeInt(x);
    buf.writeInt(y);
    buf.writeInt(z);
  }

  @Override
  public void fromBytes(ByteBuf buffer) {
    x = buffer.readInt();
    y = buffer.readInt();
    z = buffer.readInt();
  }

  @Override
  public IMessage onMessage(PacketOpenAuthGui message, MessageContext ctx) {
    ctx.getServerHandler().playerEntity.openGui(EnderIO.instance, GuiHandler.GUI_ID_TRAVEL_AUTH, ctx.getServerHandler().playerEntity.worldObj, message.x, message.y, message.z);
    return null;
  }
}
