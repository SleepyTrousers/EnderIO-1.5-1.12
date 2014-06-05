package crazypants.enderio.machine;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import crazypants.enderio.teleport.packet.PacketAccessMode;

public class PacketIoMode implements IMessage, IMessageHandler<PacketIoMode, IMessage> {

  private int x;
  private int y;
  private int z;
  private IoMode mode;
  private ForgeDirection face;

  public PacketIoMode() {
  }

  public PacketIoMode(IIoConfigurable cont, ForgeDirection face) {
    this.x = cont.getLocation().x;
    this.y = cont.getLocation().y;
    this.z = cont.getLocation().z;
    this.face = face;
    mode = cont.getIoMode(face);
  }

  @Override
  public void toBytes(ByteBuf buf) {
    buf.writeInt(x);
    buf.writeInt(y);
    buf.writeInt(z);
    buf.writeShort((short) mode.ordinal());
    buf.writeShort((short) face.ordinal());

  }

  @Override
  public void fromBytes(ByteBuf buf) {
    x = buf.readInt();
    y = buf.readInt();
    z = buf.readInt();
    mode = IoMode.values()[buf.readShort()];
    face = ForgeDirection.values()[buf.readShort()];
  }

  @Override
  public IMessage onMessage(PacketIoMode message, MessageContext ctx) {
    EntityPlayer player = ctx.getServerHandler().playerEntity;
    TileEntity te = player.worldObj.getTileEntity(message.x, message.y, message.z);
    if(te instanceof IIoConfigurable) {
      IIoConfigurable me = (IIoConfigurable) te;
      me.setIoMode(message.face, message.mode);
      player.worldObj.markBlockForUpdate(message.x, message.y, message.z);
    }
    return null;
  }

  private void handle(EntityPlayer player) {

  }

}
