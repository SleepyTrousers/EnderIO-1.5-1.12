package crazypants.enderio.teleport.packet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import crazypants.enderio.api.teleport.ITravelAccessable;
import crazypants.enderio.network.PacketUtil;
import io.netty.buffer.ByteBuf;

public class PacketVisibility implements IMessage, IMessageHandler<PacketVisibility, IMessage> {

    int x, y, z;
    boolean visible;

    @SuppressWarnings("unused")
    public PacketVisibility() {}

    public PacketVisibility(int x, int y, int z, boolean visible) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.visible = visible;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
        buf.writeBoolean(visible);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        x = buf.readInt();
        y = buf.readInt();
        z = buf.readInt();
        visible = buf.readBoolean();
    }

    @Override
    public IMessage onMessage(PacketVisibility message, MessageContext ctx) {
        EntityPlayer player = ctx.getServerHandler().playerEntity;
        TileEntity te = player.worldObj.getTileEntity(message.x, message.y, message.z);
        if (PacketUtil.isInvalidPacketForGui(ctx, te, getClass())) return null;
        if (te instanceof ITravelAccessable) {
            ((ITravelAccessable) te).setVisible(message.visible);
            player.worldObj.markBlockForUpdate(message.x, message.y, message.z);
            player.worldObj.markTileEntityChunkModified(message.x, message.y, message.z, te);
        }
        return null;
    }
}
