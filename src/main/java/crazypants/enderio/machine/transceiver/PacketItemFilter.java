package crazypants.enderio.machine.transceiver;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

import com.enderio.core.common.network.MessageTileEntity;
import com.enderio.core.common.network.NetworkUtil;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import crazypants.enderio.conduit.item.filter.ItemFilter;
import crazypants.enderio.network.PacketUtil;
import io.netty.buffer.ByteBuf;

public class PacketItemFilter extends MessageTileEntity<TileTransceiver>
        implements IMessageHandler<PacketItemFilter, IMessage> {

    private boolean isSend;
    private ItemFilter filter;

    public PacketItemFilter() {}

    public PacketItemFilter(TileTransceiver te, boolean isSend) {
        super(te);
        this.isSend = isSend;
        if (isSend) {
            filter = te.getSendItemFilter();
        } else {
            filter = te.getReceiveItemFilter();
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        super.toBytes(buf);
        buf.writeBoolean(isSend);
        NBTTagCompound tag = new NBTTagCompound();
        filter.writeToNBT(tag);
        NetworkUtil.writeNBTTagCompound(tag, buf);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        super.fromBytes(buf);
        isSend = buf.readBoolean();
        NBTTagCompound tag = NetworkUtil.readNBTTagCompound(buf);
        filter = new ItemFilter();
        filter.readFromNBT(tag);
    }

    @Override
    public IMessage onMessage(PacketItemFilter message, MessageContext ctx) {
        EntityPlayer player = ctx.getServerHandler().playerEntity;
        TileTransceiver tile = message.getTileEntity(player.worldObj);
        if (PacketUtil.isInvalidPacketForGui(ctx, tile, getClass())) return null;
        boolean isSend = message.isSend;
        ItemFilter filter = message.filter;
        if (filter != null) {
            if (isSend) {
                tile.setSendItemFilter(filter);
            } else {
                tile.setRecieveItemFilter(filter);
            }
        }
        return null;
    }
}
