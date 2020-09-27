package crazypants.enderio.machines.machine.farm;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import crazypants.enderio.base.config.config.PersonalConfig;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketFarmAction implements IMessage {

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

  public static class Handler implements IMessageHandler<PacketFarmAction, IMessage> {

    @Override
    public IMessage onMessage(PacketFarmAction message, MessageContext ctx) {
      if (PersonalConfig.machineParticlesEnabled.get()) {
        for (BlockPos bc : message.coords) {
          for (int i = 0; i < 15; i++) {
            double xOff = 0.5 + (rand.nextDouble() - 0.5) * 1.1;
            double yOff = 0.5 + (rand.nextDouble() - 0.5) * 0.2;
            double zOff = 0.5 + (rand.nextDouble() - 0.5) * 1.1;
            Minecraft.getMinecraft().world.spawnParticle(EnumParticleTypes.PORTAL, bc.getX() + xOff, bc.getY() + yOff, bc.getZ() + zOff,
                (rand.nextDouble() - 0.5) * 1.5, -rand.nextDouble(), (rand.nextDouble() - 0.5) * 1.5);
          }
        }
      }
      return null;
    }
  }
}
