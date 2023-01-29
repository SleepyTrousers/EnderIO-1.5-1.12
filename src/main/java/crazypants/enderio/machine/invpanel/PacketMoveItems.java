package crazypants.enderio.machine.invpanel;

import net.minecraft.entity.player.EntityPlayerMP;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class PacketMoveItems implements IMessage, IMessageHandler<PacketMoveItems, IMessage> {

    private int fromSlot;
    private int toSlotStart;
    private int toSlotEnd;
    private int amount;

    public PacketMoveItems(int fromSlot, int toSlotStart, int toSlotEnd, int amount) {
        this.fromSlot = fromSlot;
        this.toSlotStart = toSlotStart;
        this.toSlotEnd = toSlotEnd;
        this.amount = amount;
    }

    public PacketMoveItems() {}

    @Override
    public void fromBytes(ByteBuf bb) {
        fromSlot = bb.readShort();
        toSlotStart = bb.readShort();
        toSlotEnd = bb.readShort();
        amount = bb.readShort();
    }

    @Override
    public void toBytes(ByteBuf bb) {
        bb.writeShort(fromSlot);
        bb.writeShort(toSlotStart);
        bb.writeShort(toSlotEnd);
        bb.writeShort(amount);
    }

    @Override
    public IMessage onMessage(PacketMoveItems message, MessageContext ctx) {
        EntityPlayerMP player = ctx.getServerHandler().playerEntity;
        if (player.openContainer instanceof InventoryPanelContainer) {
            InventoryPanelContainer ipc = (InventoryPanelContainer) player.openContainer;
            ipc.executeMoveItems(message.fromSlot, message.toSlotStart, message.toSlotEnd, message.amount);
        }
        return null;
    }
}
