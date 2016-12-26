package crazypants.enderio.machine.farm;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import crazypants.util.ClientUtil;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketFarmAction implements IMessage, IMessageHandler<PacketFarmAction, IMessage> {

  private static Random rand = new Random();

  private List<BlockPos> coords;

  public PacketFarmAction() {
  }

  public PacketFarmAction(List<BlockPos> coords) {
    this.coords = coords;
  }

  public PacketFarmAction(BlockPos bc) {
    this.coords = new ArrayList<BlockPos>(1);
    this.coords.add(bc);
  }

  @Override
  public void toBytes(ByteBuf buffer) {
    int size = coords.size();
    buffer.writeInt(size);
    for (BlockPos coord : coords) {
      buffer.writeLong(coord.toLong());
    }

  }

  @Override
  public void fromBytes(ByteBuf buffer) {
    int size = buffer.readInt();
    coords = new ArrayList<BlockPos>(size);
    for (int i = 0; i < size; i++) {
      coords.add(BlockPos.fromLong(buffer.readLong()));
    }
  }

  @Override
  public IMessage onMessage(PacketFarmAction message, MessageContext ctx) {
    for (BlockPos bc : message.coords) {
      for (int i = 0; i < 15; i++) {
        ClientUtil.spawnFarmParcticles(rand, bc);
      }
    }
    return null;
  }

}
