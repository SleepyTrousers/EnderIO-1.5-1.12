package crazypants.enderio.transceiver;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import com.enderio.core.common.network.MessageTileEntity;
import com.enderio.core.common.network.NetworkUtil;

public class PacketSendRecieveChannel extends MessageTileEntity<TileTransceiver> implements IMessageHandler<PacketSendRecieveChannel, IMessage> {

  private boolean isSend;
  private boolean isAdd;
  private Channel channel;

  public PacketSendRecieveChannel() {
  }

  public PacketSendRecieveChannel(TileTransceiver te, boolean isSend, boolean isAdd, Channel channel) {
    super(te);
    this.isSend = isSend;
    this.isAdd = isAdd;
    this.channel = channel;
  }

  @Override
  public void toBytes(ByteBuf buf) {
    super.toBytes(buf);
    buf.writeBoolean(isSend);
    buf.writeBoolean(isAdd);
    NBTTagCompound tag = new NBTTagCompound();
    channel.writeToNBT(tag);
    NetworkUtil.writeNBTTagCompound(tag, buf);
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    super.fromBytes(buf);
    isSend = buf.readBoolean();
    isAdd = buf.readBoolean();
    NBTTagCompound tag = NetworkUtil.readNBTTagCompound(buf);
    channel = Channel.readFromNBT(tag);
  }

  @Override
  public IMessage onMessage(PacketSendRecieveChannel message, MessageContext ctx) {
    EntityPlayer player = ctx.getServerHandler().playerEntity;
    TileTransceiver tile = message.getTileEntity(player.world);
    if (tile != null && message.channel != null) {
      if (message.isSend) {
        if (message.isAdd) {
          tile.addSendChanel(message.channel);
        } else {
          tile.removeSendChanel(message.channel);
        }
      } else {
        if (message.isAdd) {
          tile.addRecieveChanel(message.channel);
        } else {
          tile.removeRecieveChanel(message.channel);
        }
      }
    }
    return null;
  }

}
