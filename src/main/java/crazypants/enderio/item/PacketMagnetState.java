package crazypants.enderio.item;

import net.minecraft.entity.player.EntityPlayerMP;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class PacketMagnetState implements IMessage, IMessageHandler<PacketMagnetState, IMessage> {

    public enum SlotType {
        INVENTORY,
        ARMOR,
        BAUBLES
    }

    public PacketMagnetState() {}

    private boolean isActive;
    private SlotType type;
    private int slot;

    public PacketMagnetState(SlotType slottype, int slot, boolean isActive) {
        this.type = slottype;
        this.slot = slot;
        this.isActive = isActive;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeShort(type.ordinal());
        buf.writeInt(slot);
        buf.writeBoolean(isActive);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        type = SlotType.values()[buf.readShort()];
        slot = buf.readInt();
        isActive = buf.readBoolean();
    }

    @Override
    public IMessage onMessage(PacketMagnetState message, MessageContext ctx) {
        EntityPlayerMP player = ctx.getServerHandler().playerEntity;
        MagnetController.setMagnetActive(player, message.type, message.slot, message.isActive);
        return null;
    }
}
