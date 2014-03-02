package crazypants.enderio.machine.hypercube;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;
import cpw.mods.fml.common.network.ByteBufUtils;
import crazypants.enderio.EnderIO;
import crazypants.enderio.network.IPacketEio;

public class PacketAddRemoveChannel implements IPacketEio {

  private boolean isAdd;
  private Channel channel;

  public PacketAddRemoveChannel() {
  }

  public PacketAddRemoveChannel(boolean isAdd, Channel channel) {
    this.isAdd = isAdd;
    this.channel = channel;
  }

  @Override
  public void encode(ChannelHandlerContext ctx, ByteBuf buf) {
    buf.writeBoolean(isAdd);
    buf.writeBoolean(channel.isPublic());
    ByteBufUtils.writeUTF8String(buf, channel.name);
    if(!channel.isPublic()) {
      ByteBufUtils.writeUTF8String(buf, channel.user);
    }
  }

  @Override
  public void decode(ChannelHandlerContext ctx, ByteBuf data) {
    isAdd = data.readBoolean();

    boolean isPublic = data.readBoolean();
    String name = ByteBufUtils.readUTF8String(data);
    String user = null;
    if(!isPublic) {
      user = ByteBufUtils.readUTF8String(data);
    }
    channel = new Channel(name, user);
  }

  @Override
  public void handleClientSide(EntityPlayer player) {
    if(isAdd) {
      ClientChannelRegister.instance.channelAdded(channel);
    } else {
      ClientChannelRegister.instance.channelRemoved(channel);
    }
  }

  @Override
  public void handleServerSide(EntityPlayer player) {
    if(isAdd) {
      HyperCubeRegister.instance.addChannel(channel);
    } else {
      HyperCubeRegister.instance.removeChannel(channel);
    }
    EnderIO.packetPipeline.sendToAll(new PacketAddRemoveChannel(isAdd, channel));
  }

}
