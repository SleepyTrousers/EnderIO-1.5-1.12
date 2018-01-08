package crazypants.enderio.base.transceiver;

import com.enderio.core.common.network.NetworkUtil;
import com.enderio.core.common.util.UserIdent;

import crazypants.enderio.base.network.PacketHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class PacketAddRemoveChannel implements IMessage {

  private boolean isAdd;
  private Channel channel;

  public PacketAddRemoveChannel() {
  }

  public PacketAddRemoveChannel(Channel channel, boolean isAdd) {
    this.channel = channel;
    this.isAdd = isAdd;
  }

  @Override
  public void toBytes(ByteBuf buf) {
    buf.writeBoolean(isAdd);
    NBTTagCompound nbt = new NBTTagCompound();
    channel.writeToNBT(nbt);
    NetworkUtil.writeNBTTagCompound(nbt, buf);
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    isAdd = buf.readBoolean();
    NBTTagCompound tag = NetworkUtil.readNBTTagCompound(buf);
    channel = Channel.readFromNBT(tag);
  }

  public static class Handler implements IMessageHandler<PacketAddRemoveChannel, IMessage> {

    @Override
    public IMessage onMessage(PacketAddRemoveChannel message, MessageContext ctx) {
      if (ctx.side == Side.SERVER) {
        if (!message.channel.getUser().equals(UserIdent.NOBODY) && !message.channel.getUser().equals(ctx.getServerHandler().player.getGameProfile())) {
          ctx.getServerHandler().player.connection.disconnect("Don't mess with other players' channels, you cheat.");
          return null;
        }
        if (message.isAdd) {
          ServerChannelRegister.instance.addChannel(message.channel);
        } else {
          ServerChannelRegister.instance.removeChannel(message.channel);
        }
        PacketHandler.INSTANCE.sendToAll(new PacketAddRemoveChannel(message.channel, message.isAdd));
      } else {
        if (message.isAdd) {
          ClientChannelRegister.instance.addChannel(message.channel);
        } else {
          ClientChannelRegister.instance.removeChannel(message.channel);
        }
      }
      return null;
    }

  }

}
