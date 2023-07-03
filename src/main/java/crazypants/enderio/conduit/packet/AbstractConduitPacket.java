package crazypants.enderio.conduit.packet;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.conduit.IConduitBundle;
import crazypants.enderio.network.PacketUtil;
import io.netty.buffer.ByteBuf;

public class AbstractConduitPacket<T extends IConduit> extends AbstractConduitBundlePacket {

    protected ConTypeEnum conType;

    public AbstractConduitPacket() {}

    public AbstractConduitPacket(TileEntity tile, ConTypeEnum conType) {
        super(tile);
        this.conType = conType;
    }

    protected Class<? extends IConduit> getConType() {
        return conType.getBaseType();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        super.toBytes(buf);
        buf.writeShort(conType.ordinal());
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        super.fromBytes(buf);
        short ordinal = buf.readShort();
        conType = ConTypeEnum.values()[ordinal];
    }

    @SuppressWarnings("unchecked")
    protected T getTileCasted(MessageContext ctx) {
        World world = getWorld(ctx);
        if (world == null) {
            return null;
        }
        TileEntity te = world.getTileEntity(x, y, z);
        if (!(te instanceof IConduitBundle)) {
            return null;
        }

        IConduitBundle bundle = (IConduitBundle) te;
        return (T) bundle.getConduit(getConType());
    }

    /**
     * Validates if TileEntity received from client is actually the one player is interacting with. It prevents
     * malicious user disturbing random machine settings etc.
     */
    protected static boolean isInvalidPacketForGui(AbstractConduitPacket<?> message, MessageContext ctx) {
        return PacketUtil.isInvalidPacketForGui(ctx, message.getTileEntity(message.getWorld(ctx)), message.getClass());
    }
}
