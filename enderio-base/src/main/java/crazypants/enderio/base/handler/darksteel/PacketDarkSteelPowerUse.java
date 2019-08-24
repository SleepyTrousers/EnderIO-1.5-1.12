package crazypants.enderio.base.handler.darksteel;

import crazypants.enderio.util.EnumReader;
import io.netty.buffer.ByteBuf;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketDarkSteelPowerUse implements IMessage {

  private int powerUse;
  private EntityEquipmentSlot armorType;

  public PacketDarkSteelPowerUse() {
  }

  public PacketDarkSteelPowerUse(int powerUse, EntityEquipmentSlot armorType) {
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
    armorType = EnumReader.get(EntityEquipmentSlot.class, buffer.readShort());
  }

  public static class Handler implements IMessageHandler<PacketDarkSteelPowerUse, IMessage> {

    @Override
    public IMessage onMessage(PacketDarkSteelPowerUse message, MessageContext ctx) {
      DarkSteelController.usePlayerEnergy(ctx.getServerHandler().player, message.armorType, Math.abs(message.powerUse));
      ctx.getServerHandler().player.fallDistance = 0;
      return null;
    }
  }
}
