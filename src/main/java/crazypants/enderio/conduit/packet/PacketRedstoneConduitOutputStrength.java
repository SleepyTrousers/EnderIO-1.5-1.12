package crazypants.enderio.conduit.packet;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import crazypants.enderio.conduit.redstone.IInsulatedRedstoneConduit;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.common.util.ForgeDirection;

public class PacketRedstoneConduitOutputStrength extends AbstractConduitPacket<IInsulatedRedstoneConduit>
        implements IMessageHandler<PacketRedstoneConduitOutputStrength, IMessage> {

    private ForgeDirection dir;
    private boolean isStrong;

    public PacketRedstoneConduitOutputStrength() {}

    public PacketRedstoneConduitOutputStrength(IInsulatedRedstoneConduit con, ForgeDirection dir) {
        super(con.getBundle().getEntity(), ConTypeEnum.REDSTONE);
        this.dir = dir;
        isStrong = con.isOutputStrong(dir);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        super.toBytes(buf);
        buf.writeShort(dir.ordinal());
        buf.writeBoolean(isStrong);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        super.fromBytes(buf);
        dir = ForgeDirection.values()[buf.readShort()];
        isStrong = buf.readBoolean();
    }

    @Override
    public IMessage onMessage(PacketRedstoneConduitOutputStrength message, MessageContext ctx) {
        IInsulatedRedstoneConduit tile = message.getTileCasted(ctx);
        if (tile != null) {
            tile.setOutputStrength(message.dir, message.isStrong);
            // message.getWorld(ctx).markBlockForUpdate(message.x, message.y, message.z);
        }
        return null;
    }
}
