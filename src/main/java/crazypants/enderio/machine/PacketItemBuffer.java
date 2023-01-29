package crazypants.enderio.machine;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;

import com.enderio.core.common.util.BlockCoord;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class PacketItemBuffer implements IMessage, IMessageHandler<PacketItemBuffer, IMessage> {

    private int x;
    private int y;
    private int z;
    boolean bufferStacks;

    public PacketItemBuffer() {}

    public PacketItemBuffer(IItemBuffer buffer) {
        BlockCoord bc = buffer.getLocation();
        x = bc.x;
        y = bc.y;
        z = bc.z;
        bufferStacks = buffer.isBufferStacks();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
        buf.writeBoolean(bufferStacks);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        x = buf.readInt();
        y = buf.readInt();
        z = buf.readInt();
        bufferStacks = buf.readBoolean();
    }

    @Override
    public IMessage onMessage(PacketItemBuffer message, MessageContext ctx) {
        EntityPlayer player = ctx.getServerHandler().playerEntity;
        TileEntity te = player.worldObj.getTileEntity(message.x, message.y, message.z);
        if (te instanceof IItemBuffer) {
            ((IItemBuffer) te).setBufferStacks(message.bufferStacks);
        }
        return null;
    }
}
