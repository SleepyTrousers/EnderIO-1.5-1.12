package crazypants.enderio.teleport.packet;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import crazypants.enderio.EnderIO;
import crazypants.enderio.GuiHandler;
import io.netty.buffer.ByteBuf;

public class PacketOpenAuthGui implements IMessage, IMessageHandler<PacketOpenAuthGui, IMessage> {

    int x;
    int y;
    int z;

    public PacketOpenAuthGui() {}

    public PacketOpenAuthGui(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
    }

    @Override
    public void fromBytes(ByteBuf buffer) {
        x = buffer.readInt();
        y = buffer.readInt();
        z = buffer.readInt();
    }

    public IMessage onMessage(PacketOpenAuthGui message, cpw.mods.fml.common.network.simpleimpl.MessageContext ctx) {
        ctx.getServerHandler()
                .playerEntity
                .openGui(
                        EnderIO.instance,
                        GuiHandler.GUI_ID_TRAVEL_AUTH,
                        ctx.getServerHandler().playerEntity.worldObj,
                        message.x,
                        message.y,
                        message.z);
        return null;
    }
}
