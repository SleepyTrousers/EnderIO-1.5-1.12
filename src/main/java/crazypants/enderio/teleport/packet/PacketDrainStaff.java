package crazypants.enderio.teleport.packet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;
import crazypants.enderio.EnderIO;
import crazypants.enderio.network.IPacketEio;
import crazypants.enderio.teleport.ItemTravelStaff;

public class PacketDrainStaff implements IPacketEio {

  int powerUse;

  public PacketDrainStaff() {
  }

  public PacketDrainStaff(int powerUse) {
    this.powerUse = powerUse;
  }

  @Override
  public void encode(ChannelHandlerContext ctx, ByteBuf buffer) {
    buffer.writeInt(powerUse);
  }

  @Override
  public void decode(ChannelHandlerContext ctx, ByteBuf buffer) {
    powerUse = buffer.readInt();
  }

  @Override
  public void handleClientSide(EntityPlayer player) {
  }

  @Override
  public void handleServerSide(EntityPlayer ep) {
    if(ItemTravelStaff.isEquipped(ep)) {
      EnderIO.itemTravelStaff.extractInternal(ep.getCurrentEquippedItem(), powerUse);
    }
  }

}
