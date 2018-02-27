package crazypants.enderio.base.handler.darksteel;

import io.netty.buffer.ByteBuf;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketDarkSteelPowerPacket implements IMessage {

  private int powerUse;
  private EntityEquipmentSlot armorType;

  public PacketDarkSteelPowerPacket() {
  }

  public PacketDarkSteelPowerPacket(int powerUse, EntityEquipmentSlot armorType) {
    this.powerUse = powerUse;
    this.armorType = armorType;
  }

  @Override
  public void toBytes(ByteBuf buffer) {
    buffer.writeInt(powerUse);
    buffer.writeShort((short) armorType.ordinal());
  }

  @Override
  public void fromBytes(ByteBuf buffer) {
    powerUse = buffer.readInt();
    armorType = EntityEquipmentSlot.values()[buffer.readShort()];
  }

  public static class Handler implements IMessageHandler<PacketDarkSteelPowerPacket, IMessage> {

    @Override
    public IMessage onMessage(PacketDarkSteelPowerPacket message, MessageContext ctx) {
      DarkSteelController.usePlayerEnergy(ctx.getServerHandler().player, message.armorType, message.powerUse);
      ctx.getServerHandler().player.fallDistance = 0;
      return null;
    }
  }
}
