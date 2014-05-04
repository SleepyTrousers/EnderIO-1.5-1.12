package crazypants.enderio.machine.farm;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.entity.player.EntityPlayer;
import crazypants.enderio.network.IPacketEio;
import crazypants.util.BlockCoord;

public class PacketFarmAction implements IPacketEio {

  private static Random rand = new Random();

  private List<BlockCoord> coords;

  public PacketFarmAction() {
  }

  public PacketFarmAction(List<BlockCoord> coords) {
    this.coords = coords;
  }

  @Override
  public void encode(ChannelHandlerContext ctx, ByteBuf buffer) {
    int size = coords.size();
    buffer.writeInt(size);
    for (BlockCoord coord : coords) {
      buffer.writeInt(coord.x);
      buffer.writeInt(coord.y);
      buffer.writeInt(coord.z);
    }

  }

  @Override
  public void decode(ChannelHandlerContext ctx, ByteBuf buffer) {
    int size = buffer.readInt();
    coords = new ArrayList<BlockCoord>(size);
    for (int i = 0; i < size; i++) {
      coords.add(new BlockCoord(buffer.readInt(), buffer.readInt(), buffer.readInt()));
    }
  }

  @Override
  public void handleClientSide(EntityPlayer player) {
    for (BlockCoord bc : coords) {
      for (int i = 0; i < 15; i++) {
        double xOff = 0.5 + (rand.nextDouble() - 0.5) * 1.1;
        double yOff = 0.5 + (rand.nextDouble() - 0.5) * 0.2;
        double zOff = 0.5 + (rand.nextDouble() - 0.5) * 1.1;
        player.worldObj.spawnParticle("portal", bc.x + xOff, bc.y + yOff, bc.z + zOff,
            (rand.nextDouble() - 0.5) * 1.5, -rand.nextDouble(), (rand.nextDouble() - 0.5) * 1.5);
      }
    }

  }

  @Override
  public void handleServerSide(EntityPlayer player) {
  }

}
