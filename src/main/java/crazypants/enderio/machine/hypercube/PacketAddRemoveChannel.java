package crazypants.enderio.machine.hypercube;

import io.netty.buffer.ByteBuf;

import com.enderio.core.common.util.PlayerUtil;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import crazypants.enderio.network.PacketHandler;

public class PacketAddRemoveChannel implements IMessage, IMessageHandler<PacketAddRemoveChannel, IMessage> {

  private boolean isAdd;
  private Channel channel;

  public PacketAddRemoveChannel() {
  }

  public PacketAddRemoveChannel(boolean isAdd, Channel channel) {
    this.isAdd = isAdd;
    this.channel = channel;
  }

  @Override
  public void toBytes(ByteBuf buf) {
    buf.writeBoolean(isAdd);
    buf.writeBoolean(channel.isPublic());
    ByteBufUtils.writeUTF8String(buf, channel.name);
    if(!channel.isPublic()) {
      ByteBufUtils.writeUTF8String(buf, channel.user.toString());
    }
  }

  @Override
  public void fromBytes(ByteBuf data) {
    isAdd = data.readBoolean();

    boolean isPublic = data.readBoolean();
    String name = ByteBufUtils.readUTF8String(data);
    String user = null;
    if(!isPublic) {
      user = ByteBufUtils.readUTF8String(data);
    }
    channel = new Channel(name, PlayerUtil.getPlayerUIDUnstable(user));
  }

  @Override
  public IMessage onMessage(PacketAddRemoveChannel message, MessageContext ctx) {
    if(FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER) {
      if(message.isAdd) {
        HyperCubeRegister.instance.addChannel(message.channel);
      } else {
        HyperCubeRegister.instance.removeChannel(message.channel);
      }
      PacketHandler.INSTANCE.sendToAll(new PacketAddRemoveChannel(message.isAdd, message.channel));
    } else {
      if(message.isAdd) {
        ClientChannelRegister.instance.channelAdded(message.channel);
      } else {
        ClientChannelRegister.instance.channelRemoved(message.channel);
      }
    }
    return null;
  }

}
