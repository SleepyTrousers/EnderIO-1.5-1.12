package crazypants.enderio.conduit.packet;

import net.minecraftforge.common.util.ForgeDirection;

import com.enderio.core.common.util.DyeColor;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import crazypants.enderio.conduit.IExtractor;
import crazypants.enderio.machine.RedstoneControlMode;
import io.netty.buffer.ByteBuf;

public class PacketExtractMode extends AbstractConduitPacket<IExtractor>
        implements IMessageHandler<PacketExtractMode, IMessage> {

    private ForgeDirection dir;
    private RedstoneControlMode mode;
    private DyeColor color;

    public PacketExtractMode() {}

    public PacketExtractMode(IExtractor con, ForgeDirection dir) {
        super(con.getBundle().getEntity(), ConTypeEnum.get(con));
        this.dir = dir;
        mode = con.getExtractionRedstoneMode(dir);
        color = con.getExtractionSignalColor(dir);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        super.toBytes(buf);
        buf.writeShort(dir.ordinal());
        buf.writeShort(mode.ordinal());
        buf.writeShort(color.ordinal());
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        super.fromBytes(buf);
        dir = ForgeDirection.values()[buf.readShort()];
        mode = RedstoneControlMode.values()[buf.readShort()];
        color = DyeColor.values()[buf.readShort()];
    }

    @Override
    public IMessage onMessage(PacketExtractMode message, MessageContext ctx) {
        if (isInvalidPacketForGui(message, ctx)) return null;
        message.getTileCasted(ctx).setExtractionRedstoneMode(message.mode, message.dir);
        message.getTileCasted(ctx).setExtractionSignalColor(message.dir, message.color);
        message.getWorld(ctx).markBlockForUpdate(message.x, message.y, message.z);
        return null;
    }
}
