package crazypants.enderio.teleport.packet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;
import crazypants.enderio.EnderIO;
import crazypants.enderio.GuiHandler;
import crazypants.enderio.network.IPacketEio;

public class PacketOpenAuthGui implements IPacketEio {

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
  public void encode(ChannelHandlerContext ctx, ByteBuf buffer) {
    buffer.writeInt(x);
    buffer.writeInt(y);
    buffer.writeInt(z);
  }

  @Override
  public void decode(ChannelHandlerContext ctx, ByteBuf buffer) {
    x = buffer.readInt();
    y = buffer.readInt();
    z = buffer.readInt();
  }

  @Override
  public void handleClientSide(EntityPlayer player) {

  }

  @Override
  public void handleServerSide(EntityPlayer ep) {
    ep.openGui(EnderIO.instance, GuiHandler.GUI_ID_TRAVEL_AUTH, ep.worldObj, x, y, z);
  }

}
