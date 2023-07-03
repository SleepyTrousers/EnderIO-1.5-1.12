package crazypants.enderio.conduit.gui;

import net.minecraftforge.common.util.ForgeDirection;

import com.enderio.core.common.util.DyeColor;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import crazypants.enderio.conduit.liquid.AbstractEnderLiquidConduit;
import crazypants.enderio.conduit.liquid.ILiquidConduit;
import crazypants.enderio.conduit.packet.AbstractConduitPacket;
import crazypants.enderio.conduit.packet.ConTypeEnum;
import io.netty.buffer.ByteBuf;

public class PacketFluidChannel extends AbstractConduitPacket<ILiquidConduit>
        implements IMessageHandler<PacketFluidChannel, IMessage> {

    private ForgeDirection dir;
    private boolean isInput;

    private DyeColor channel;

    public PacketFluidChannel() {}

    public PacketFluidChannel(AbstractEnderLiquidConduit eConduit, ForgeDirection dir, boolean isInput,
            DyeColor channel) {
        super(eConduit.getBundle().getEntity(), ConTypeEnum.FLUID);
        this.dir = dir;
        this.isInput = isInput;
        this.channel = channel;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        super.toBytes(buf);
        buf.writeShort(dir.ordinal());
        buf.writeBoolean(isInput);
        buf.writeShort(channel.ordinal());
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        super.fromBytes(buf);
        dir = ForgeDirection.values()[buf.readShort()];
        isInput = buf.readBoolean();
        channel = DyeColor.values()[buf.readShort()];
    }

    @Override
    public IMessage onMessage(PacketFluidChannel message, MessageContext ctx) {
        if (isInvalidPacketForGui(message, ctx)) return null;
        ILiquidConduit conduit = message.getTileCasted(ctx);
        if (!(conduit instanceof AbstractEnderLiquidConduit)) {
            return null;
        }
        AbstractEnderLiquidConduit eCon = (AbstractEnderLiquidConduit) conduit;

        if (message.isInput) eCon.setInputColor(message.dir, message.channel);
        else eCon.setOutputColor(message.dir, message.channel);

        message.getWorld(ctx).markBlockForUpdate(message.x, message.y, message.z);
        return null;
    }
}
