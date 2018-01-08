package crazypants.enderio.machines.machine.transceiver;

import javax.annotation.Nonnull;

import com.enderio.core.common.network.MessageTileEntity;
import com.enderio.core.common.network.NetworkUtil;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.transceiver.ChannelList;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketSendRecieveChannelList extends MessageTileEntity<TileTransceiver> {

  private boolean isSend;
  private ChannelList channels;

  public PacketSendRecieveChannelList() {
  }

  public PacketSendRecieveChannelList(@Nonnull TileTransceiver te, boolean isSend) {
    super(te);
    this.isSend = isSend;
    if (isSend) {
      this.channels = te.getSendChannels();
    } else {
      this.channels = te.getReceiveChannels();
    }
  }

  // TODO can this use the @Store API ?
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
    channels = new ChannelList();
    TileTransceiver.readChannels(root, channels, "chans");
  }

  public static class Handler implements IMessageHandler<PacketSendRecieveChannelList, IMessage> {

    @Override
    public IMessage onMessage(PacketSendRecieveChannelList message, MessageContext ctx) {
      EntityPlayer player = EnderIO.proxy.getClientPlayer();
      TileTransceiver tile = message.getTileEntity(player.world);
      if (tile != null) {
        if (message.isSend) {
          tile.setSendChannels(message.channels);
        } else {
          tile.setRecieveChannels(message.channels);
        }
      }
      return null;
    }
  }
}
