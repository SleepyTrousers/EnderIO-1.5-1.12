package crazypants.enderio.conduit.packet;

import net.minecraftforge.common.util.ForgeDirection;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import crazypants.enderio.conduit.liquid.AbstractEnderLiquidConduit;
import crazypants.enderio.conduit.liquid.ILiquidConduit;
import io.netty.buffer.ByteBuf;

public class PacketRoundRobinMode extends AbstractConduitPacket<ILiquidConduit>
        implements IMessageHandler<PacketRoundRobinMode, IMessage> {

    private ForgeDirection dir;
    private boolean roundRobin;

    public PacketRoundRobinMode() {}

    public PacketRoundRobinMode(AbstractEnderLiquidConduit eConduit, ForgeDirection dir) {
        super(eConduit.getBundle().getEntity(), ConTypeEnum.FLUID);
        this.dir = dir;
        roundRobin = eConduit.isRoundRobin(dir);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        super.toBytes(buf);
        buf.writeShort(dir.ordinal());
        buf.writeBoolean(roundRobin);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        super.fromBytes(buf);
        dir = ForgeDirection.values()[buf.readShort()];
        roundRobin = buf.readBoolean();
    }

    @Override
    public IMessage onMessage(PacketRoundRobinMode message, MessageContext ctx) {
        if (isInvalidPacketForGui(message, ctx)) return null;
        final ILiquidConduit conduit = message.getTileCasted(ctx);
        if (conduit instanceof AbstractEnderLiquidConduit) {
            ((AbstractEnderLiquidConduit) conduit).setRoundRobin(message.dir, message.roundRobin);
            message.getWorld(ctx).markBlockForUpdate(message.x, message.y, message.z);
        }
        return null;
    }
}
