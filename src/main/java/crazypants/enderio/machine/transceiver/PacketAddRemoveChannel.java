package crazypants.enderio.machine.transceiver;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;

import com.enderio.core.common.network.NetworkUtil;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import crazypants.enderio.network.PacketHandler;

public class PacketAddRemoveChannel implements IMessage, IMessageHandler<PacketAddRemoveChannel, IMessage>  {

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

  @Override
  public IMessage onMessage(PacketAddRemoveChannel message, MessageContext ctx) {
    ChannelRegister register = ctx.side == Side.CLIENT ? ClientChannelRegister.instance : ServerChannelRegister.instance;
    if(message.isAdd) {
      register.addChannel(message.channel);      
    } else {
      register.removeChannel(message.channel);      
    }
    if(ctx.side == Side.SERVER) {
      PacketHandler.INSTANCE.sendToAll(new PacketAddRemoveChannel(message.channel, message.isAdd));  
    }
    return null;
  }

  

}
