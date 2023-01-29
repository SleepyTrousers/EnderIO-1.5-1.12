package crazypants.enderio.machine.spawner;

import net.minecraft.tileentity.TileEntity;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class PacketMode implements IMessage, IMessageHandler<PacketMode, IMessage> {

    private int x;
    private int y;
    private int z;

    private boolean isSpawnMode;

    public PacketMode() {}

    public PacketMode(TilePoweredSpawner tile) {
        x = tile.xCoord;
        y = tile.yCoord;
        z = tile.zCoord;
        isSpawnMode = tile.isSpawnMode();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
        buf.writeBoolean(isSpawnMode);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        x = buf.readInt();
        y = buf.readInt();
        z = buf.readInt();
        isSpawnMode = buf.readBoolean();
    }

    @Override
    public IMessage onMessage(PacketMode message, MessageContext ctx) {
        TileEntity te = ctx.getServerHandler().playerEntity.worldObj.getTileEntity(message.x, message.y, message.z);
        if (te instanceof TilePoweredSpawner) {
            TilePoweredSpawner me = (TilePoweredSpawner) te;
            me.setSpawnMode(message.isSpawnMode);
        }
        return null;
    }
}
