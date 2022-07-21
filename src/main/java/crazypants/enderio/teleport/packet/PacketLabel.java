package crazypants.enderio.teleport.packet;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import crazypants.enderio.api.teleport.ITravelAccessable;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;

public class PacketLabel implements IMessage, IMessageHandler<PacketLabel, IMessage> {

    int x;
    int y;
    int z;
    boolean labelNull;
    String label;

    public PacketLabel() {}

    public PacketLabel(int x, int y, int z, String label) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.label = label;
        labelNull = label == null || label.length() == 0;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
        buf.writeBoolean(labelNull);
        if (!labelNull) {
            ByteBufUtils.writeUTF8String(buf, label);
        }
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        x = buf.readInt();
        y = buf.readInt();
        z = buf.readInt();
        labelNull = buf.readBoolean();
        if (labelNull) {
            label = null;
        } else {
            label = ByteBufUtils.readUTF8String(buf);
        }
    }

    public IMessage onMessage(PacketLabel message, MessageContext ctx) {
        EntityPlayer player = ctx.getServerHandler().playerEntity;
        TileEntity te = player.worldObj.getTileEntity(message.x, message.y, message.z);
        if (te instanceof ITravelAccessable) {
            ((ITravelAccessable) te).setLabel(message.label);
            player.worldObj.markBlockForUpdate(message.x, message.y, message.z);
            player.worldObj.markTileEntityChunkModified(message.x, message.y, message.z, te);
        }
        return null;
    }
}
