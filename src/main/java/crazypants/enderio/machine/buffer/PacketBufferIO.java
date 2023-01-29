package crazypants.enderio.machine.buffer;

import com.enderio.core.common.network.MessageTileEntity;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class PacketBufferIO extends MessageTileEntity<TileBuffer>
        implements IMessage, IMessageHandler<PacketBufferIO, IMessage> {

    public PacketBufferIO() {}

    private int in, out;

    public PacketBufferIO(TileBuffer tile, int in, int out) {
        super(tile);
        tile.setIO(in, out);
        this.in = in;
        this.out = out;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        super.fromBytes(buf);
        this.in = buf.readInt();
        this.out = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        super.toBytes(buf);
        buf.writeInt(this.in);
        buf.writeInt(this.out);
    }

    @Override
    public IMessage onMessage(PacketBufferIO message, MessageContext ctx) {
        TileBuffer buf = message.getTileEntity(message.getWorld(ctx));
        if (buf != null) {
            buf.setIO(message.in, message.out);
        }
        return null;
    }
}
