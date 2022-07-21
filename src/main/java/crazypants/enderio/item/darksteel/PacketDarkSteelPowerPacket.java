package crazypants.enderio.item.darksteel;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class PacketDarkSteelPowerPacket implements IMessage, IMessageHandler<PacketDarkSteelPowerPacket, IMessage> {

    private int powerUse;
    private short armorType;

    public PacketDarkSteelPowerPacket() {}

    public PacketDarkSteelPowerPacket(int powerUse, int armorType) {
        this.powerUse = powerUse;
        this.armorType = (short) armorType;
    }

    @Override
    public void toBytes(ByteBuf buffer) {
        buffer.writeInt(powerUse);
        buffer.writeShort(armorType);
    }

    @Override
    public void fromBytes(ByteBuf buffer) {
        powerUse = buffer.readInt();
        armorType = buffer.readShort();
    }

    public IMessage onMessage(PacketDarkSteelPowerPacket message, MessageContext ctx) {
        DarkSteelController.instance.usePlayerEnergy(
                ctx.getServerHandler().playerEntity,
                ItemDarkSteelArmor.forArmorType(message.armorType),
                message.powerUse);
        ctx.getServerHandler().playerEntity.fallDistance = 0;
        return null;
    }
}
