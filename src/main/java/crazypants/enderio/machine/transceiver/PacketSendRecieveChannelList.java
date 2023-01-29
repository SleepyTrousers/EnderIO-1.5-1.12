package crazypants.enderio.machine.transceiver;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import com.enderio.core.common.network.MessageTileEntity;
import com.enderio.core.common.network.NetworkUtil;
import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.SetMultimap;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import crazypants.enderio.EnderIO;
import io.netty.buffer.ByteBuf;

public class PacketSendRecieveChannelList extends MessageTileEntity<TileTransceiver>
        implements IMessageHandler<PacketSendRecieveChannelList, IMessage> {

    private boolean isSend;
    private SetMultimap<ChannelType, Channel> channels;

    public PacketSendRecieveChannelList() {}

    public PacketSendRecieveChannelList(TileTransceiver te, boolean isSend) {
        super(te);
        this.isSend = isSend;
        if (isSend) {
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
        channels = MultimapBuilder.enumKeys(ChannelType.class).hashSetValues().build();
        TileTransceiver.readChannels(root, channels, "chans");
    }

    @Override
    public IMessage onMessage(PacketSendRecieveChannelList message, MessageContext ctx) {
        EntityPlayer player = EnderIO.proxy.getClientPlayer();
        TileTransceiver tile = message.getTileEntity(player.worldObj);
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
