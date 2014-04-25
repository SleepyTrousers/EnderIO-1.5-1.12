package crazypants.enderio.item.darksteel;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;
import crazypants.enderio.network.IPacketEio;

public class PacketDarkSteelPowerPacket implements IPacketEio {

  private int powerUse;
  private short armorType;

  public PacketDarkSteelPowerPacket() {
  }

  public PacketDarkSteelPowerPacket(int powerUse, int armorType) {
    this.powerUse = powerUse;
    this.armorType = (short) armorType;
  }

  @Override
  public void encode(ChannelHandlerContext ctx, ByteBuf buffer) {
    buffer.writeInt(powerUse);
    buffer.writeShort(armorType);
  }

  @Override
  public void decode(ChannelHandlerContext ctx, ByteBuf buffer) {
    powerUse = buffer.readInt();
    armorType = buffer.readShort();
  }

  @Override
  public void handleClientSide(EntityPlayer player) {
  }

  @Override
  public void handleServerSide(EntityPlayer player) {
    DarkSteelController.instance.usePlayerEnergy(player, ItemDarkSteelArmor.forArmorType(armorType), powerUse);
    player.fallDistance = 0;
  }

}
