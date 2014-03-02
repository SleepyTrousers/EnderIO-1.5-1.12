package crazypants.enderio.machine.hypercube;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import cpw.mods.fml.common.network.ByteBufUtils;
import crazypants.enderio.network.IPacketEio;

public class PacketChannelList implements IPacketEio {

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
  public void encode(ChannelHandlerContext ctx, ByteBuf buffer) {
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
  public void decode(ChannelHandlerContext ctx, ByteBuf buffer) {
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
  public void handleClientSide(EntityPlayer player) {
    if(isPrivate) {
      ClientChannelRegister.instance.setPrivateChannels(channels);
    } else {
      ClientChannelRegister.instance.setPublicChannels(channels);
    }
  }

  @Override
  public void handleServerSide(EntityPlayer player) {

  }

}
