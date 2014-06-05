package crazypants.enderio.machine.hypercube;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class PacketChannelList implements IMessage, IMessageHandler<PacketChannelList, IMessage>  {

  private boolean isPrivate;
  private List<Channel> channels;
  private String userId;

  public PacketChannelList() {
  }

  public PacketChannelList(EntityPlayer player, boolean isPrivate) {
    this(player.getGameProfile().getId(), isPrivate);
  }

  public PacketChannelList(String userId, boolean isPrivate) {
    this.userId = userId;
    this.isPrivate = isPrivate;
    if(isPrivate && userId == null || userId.trim().length() == 0) {
      throw new RuntimeException("Null user ID.");
    }

    List<Channel> res;
    if(isPrivate) {
      res = HyperCubeRegister.instance.getChannelsForUser(userId);
    } else {
      res = HyperCubeRegister.instance.getPublicChannels();
    }

    if(res != null && !res.isEmpty()) {
      channels = new ArrayList<Channel>(res);
    } else {
      channels = new ArrayList<Channel>();
    }
  }

  @Override
  public void toBytes(ByteBuf buffer) {
    buffer.writeBoolean(isPrivate);
    if(isPrivate) {
      ByteBufUtils.writeUTF8String(buffer, userId);
    }
    buffer.writeInt(channels.size());
    for (Channel channel : channels) {
      ByteBufUtils.writeUTF8String(buffer, channel.name);
    }

  }

  @Override
  public void fromBytes(ByteBuf buffer) {
    isPrivate = buffer.readBoolean();
    if(isPrivate) {
      userId = ByteBufUtils.readUTF8String(buffer);
    } else {
      userId = null;
    }
    int numChannels = buffer.readInt();
    channels = new ArrayList<Channel>();
    for (int i = 0; i < numChannels; i++) {
      channels.add(new Channel(ByteBufUtils.readUTF8String(buffer), userId));
    }

  }

  @Override
  public IMessage onMessage(PacketChannelList message, MessageContext ctx) {
    if(isPrivate) {
      ClientChannelRegister.instance.setPrivateChannels(message.channels);
    } else {
      ClientChannelRegister.instance.setPublicChannels(message.channels);
    }
    return null;
  }

  

}
