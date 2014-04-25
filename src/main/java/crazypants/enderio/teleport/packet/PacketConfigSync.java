package crazypants.enderio.teleport.packet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;
import crazypants.enderio.Config;
import crazypants.enderio.network.IPacketEio;

public class PacketConfigSync implements IPacketEio {

  @Override
  public void encode(ChannelHandlerContext ctx, ByteBuf dos) {
    dos.writeBoolean(Config.travelAnchorEnabled);
    dos.writeInt(Config.travelAnchorMaxDistance);
    dos.writeBoolean(Config.travelStaffEnabled);
    dos.writeBoolean(Config.travelStaffBlinkEnabled);
    dos.writeBoolean(Config.travelStaffBlinkThroughSolidBlocksEnabled);
    dos.writeBoolean(Config.travelStaffBlinkThroughClearBlocksEnabled);
    dos.writeInt(Config.travelStaffBlinkPauseTicks);
    dos.writeInt(Config.travelStaffMaxDistance);
    dos.writeInt(Config.travelStaffMaxBlinkDistance);
    dos.writeFloat(Config.travelStaffPowerPerBlockRF);
  }

  @Override
  public void decode(ChannelHandlerContext ctx, ByteBuf data) {
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
  }

  @Override
  public void handleClientSide(EntityPlayer player) {

  }

  @Override
  public void handleServerSide(EntityPlayer player) {

  }

}
