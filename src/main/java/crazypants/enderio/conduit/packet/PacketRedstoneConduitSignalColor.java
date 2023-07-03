package crazypants.enderio.conduit.packet;

import net.minecraftforge.common.util.ForgeDirection;

import com.enderio.core.common.util.DyeColor;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import crazypants.enderio.conduit.redstone.IInsulatedRedstoneConduit;
import io.netty.buffer.ByteBuf;

public class PacketRedstoneConduitSignalColor extends AbstractConduitPacket<IInsulatedRedstoneConduit>
        implements IMessageHandler<PacketRedstoneConduitSignalColor, IMessage> {

    private ForgeDirection dir;
    private DyeColor col;

    public PacketRedstoneConduitSignalColor() {}

    public PacketRedstoneConduitSignalColor(IInsulatedRedstoneConduit con, ForgeDirection dir) {
        super(con.getBundle().getEntity(), ConTypeEnum.REDSTONE);
        this.dir = dir;
        col = con.getSignalColor(dir);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        super.toBytes(buf);
        buf.writeShort(dir.ordinal());
        buf.writeShort(col.ordinal());
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        super.fromBytes(buf);
        dir = ForgeDirection.values()[buf.readShort()];
        col = DyeColor.values()[buf.readShort()];
    }

    @Override
    public IMessage onMessage(PacketRedstoneConduitSignalColor message, MessageContext ctx) {
        if (isInvalidPacketForGui(message, ctx)) return null;
        message.getTileCasted(ctx).setSignalColor(message.dir, message.col);
        message.getWorld(ctx).markBlockForUpdate(message.x, message.y, message.z);
        return null;
    }
}
