package crazypants.enderio.machine.transceiver;

import com.enderio.core.common.network.MessageTileEntity;
import com.enderio.core.common.network.NetworkUtil;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public class PacketSendRecieveChannel extends MessageTileEntity<TileTransceiver>
        implements IMessageHandler<PacketSendRecieveChannel, IMessage> {

    private boolean isSend;
    private boolean isAdd;
    private Channel channel;

    public PacketSendRecieveChannel() {}

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
        TileTransceiver tile = message.getTileEntity(player.worldObj);
        Channel channel = message.channel;
        boolean isSend = message.isSend;
        boolean isAdd = message.isAdd;
        if (tile != null && channel != null) {
            if (isSend) {
                if (isAdd) {
                    tile.addSendChanel(channel);
                } else {
                    tile.removeSendChanel(channel);
                }
            } else {
                if (isAdd) {
                    tile.addRecieveChanel(channel);
                } else {
                    tile.removeRecieveChanel(channel);
                }
            }
        }
        return null;
    }
}
