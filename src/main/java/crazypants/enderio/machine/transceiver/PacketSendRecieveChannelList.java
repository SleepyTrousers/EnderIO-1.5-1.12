package crazypants.enderio.machine.transceiver;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

import com.enderio.core.common.network.MessageTileEntity;
import com.enderio.core.common.network.NetworkUtil;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import crazypants.enderio.EnderIO;

public class PacketSendRecieveChannelList extends MessageTileEntity<TileTransceiver> implements IMessageHandler<PacketSendRecieveChannelList, IMessage> {

  private boolean isSend;
  private EnumMap<ChannelType, List<Channel>> channels;

  public PacketSendRecieveChannelList() {
  }

  public PacketSendRecieveChannelList(TileTransceiver te, boolean isSend) {
    super(te);
    this.isSend = isSend;
    if(isSend) {
      this.channels = te.getSendChannels();
    } else {
      this.channels = te.getReceiveChannels();
    }
  }

  @Override
  public void toBytes(ByteBuf buf) {
    super.toBytes(buf);
    buf.writeBoolean(isSend);

    NBTTagList tagList = TileTransceiver.createTagList(channels);
    NBTTagCompound root = new NBTTagCompound();
    root.setTag("chans", tagList);
    NetworkUtil.writeNBTTagCompound(root, buf);
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    super.fromBytes(buf);
    isSend = buf.readBoolean();
    NBTTagCompound root = NetworkUtil.readNBTTagCompound(buf);
    channels = new EnumMap<ChannelType, List<Channel>>(ChannelType.class);
    for(ChannelType type : ChannelType.values()) {
      channels.put(type, new ArrayList<Channel>());
    }
    TileTransceiver.readChannels(root, channels, "chans");
  }

  @Override
  public IMessage onMessage(PacketSendRecieveChannelList message, MessageContext ctx) {
    EntityPlayer player = EnderIO.proxy.getClientPlayer();
    TileTransceiver tile = message.getTileEntity(player.worldObj); 
    if(tile != null) {
      if(message.isSend) {
        tile.setSendChannels(message.channels);
      } else {
        tile.setRecieveChannels(message.channels);
      }
    }
    return null;
  }

}
