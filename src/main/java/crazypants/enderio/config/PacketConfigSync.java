package crazypants.enderio.config;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

/**
 * Syncs some configs that are only used clientside, but must use the serverside
 * value for balance purposes.
 */
public class PacketConfigSync implements IMessage, IMessageHandler<PacketConfigSync, IMessage> {

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(Config.travelAnchorEnabled);
        buf.writeInt(Config.travelAnchorMaxDistance);
        buf.writeBoolean(Config.travelStaffEnabled);
        buf.writeBoolean(Config.travelStaffBlinkEnabled);
        buf.writeBoolean(Config.travelStaffBlinkThroughSolidBlocksEnabled);
        buf.writeBoolean(Config.travelStaffBlinkThroughClearBlocksEnabled);
        buf.writeInt(Config.travelStaffBlinkPauseTicks);
        buf.writeInt(Config.travelStaffMaxDistance);
        buf.writeInt(Config.travelStaffMaxBlinkDistance);
        buf.writeFloat(Config.travelStaffPowerPerBlockRF);
        buf.writeBoolean(Config.telepadLockCoords);
        buf.writeBoolean(Config.telepadLockDimension);
    }

    @Override
    public void fromBytes(ByteBuf data) {
        Config.travelAnchorEnabled = data.readBoolean();
        Config.travelAnchorMaxDistance = data.readInt();
        Config.travelStaffEnabled = data.readBoolean();
        Config.travelStaffBlinkEnabled = data.readBoolean();
        Config.travelStaffBlinkThroughSolidBlocksEnabled = data.readBoolean();
        Config.travelStaffBlinkThroughClearBlocksEnabled = data.readBoolean();
        Config.travelStaffBlinkPauseTicks = data.readInt();
        Config.travelStaffMaxDistance = data.readInt();
        Config.travelStaffMaxBlinkDistance = data.readInt();
        Config.travelStaffPowerPerBlockRF = data.readFloat();
        Config.telepadLockCoords = data.readBoolean();
        Config.telepadLockDimension = data.readBoolean();
    }

    @Override
    public IMessage onMessage(PacketConfigSync message, MessageContext ctx) {
        return null;
    }
}
