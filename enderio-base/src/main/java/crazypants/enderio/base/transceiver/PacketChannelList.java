package crazypants.enderio.base.transceiver;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.enderio.core.common.network.NetworkUtil;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketChannelList implements IMessage {

  private final @Nonnull List<Channel> channels = new ArrayList<Channel>();

  public PacketChannelList() {
  }

  public PacketChannelList(ChannelRegister register) {
    channels.addAll(register.getAllChannels());
  }

  @Override
  public void toBytes(ByteBuf buf) {
    NBTTagList tagList = new NBTTagList();
    for (Channel chan : channels) {
      NBTTagCompound tag = new NBTTagCompound();
      chan.writeToNBT(tag);
      tagList.appendTag(tag);
    }
    NBTTagCompound root = new NBTTagCompound();
    root.setTag("chanList", tagList);
    NetworkUtil.writeNBTTagCompound(root, buf);
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    NBTTagCompound root = NetworkUtil.readNBTTagCompound(buf);
    NBTTagList tagList = (NBTTagList) root.getTag("chanList");
    for (int i = 0; i < tagList.tagCount(); i++) {
      NBTTagCompound tag = tagList.getCompoundTagAt(i);
      Channel chan = Channel.readFromNBT(tag);
      if (chan != null) {
        channels.add(chan);
      }
    }

  }

  public static class Handler implements IMessageHandler<PacketChannelList, IMessage> {

    @Override
    public IMessage onMessage(PacketChannelList message, MessageContext ctx) {
      ClientChannelRegister.instance.reset();
      for (Channel channel : message.channels) {
        ClientChannelRegister.instance.addChannel(channel);
      }
      return null;
    }

  }

}
