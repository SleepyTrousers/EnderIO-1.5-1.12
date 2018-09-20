package crazypants.enderio.base.network;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import crazypants.enderio.util.ClientUtil;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketSpawnParticles implements IMessage {

  private double posX, posY, posZ;
  private List<EnumParticleTypes> particles;
  private int count;

  public PacketSpawnParticles() {
  }

  public static void create(Entity position, EnumParticleTypes... particles) {
    create(position, 5, particles);
  }

  public static void create(Entity position, int count, EnumParticleTypes... particles) {
    PacketHandler.INSTANCE.sendToAllAround(new PacketSpawnParticles(position.posX, position.posY + position.height * 0.8, position.posZ, count, particles),
        new BlockPos(position), position.world);
  }

  public static void create(World world, BlockPos position, EnumParticleTypes... particles) {
    create(world, position, 5, particles);
  }

  public static void create(World world, BlockPos position, int count, EnumParticleTypes... particles) {
    PacketHandler.INSTANCE.sendToAllAround(new PacketSpawnParticles(position.getX() + .5, position.getY() + .5, position.getZ() + .5, count, particles),
        position, world);
  }

  public static void create(World world, double posX, double posY, double posZ, int count, EnumParticleTypes... particles) {
    PacketHandler.INSTANCE.sendToAllAround(new PacketSpawnParticles(posX, posY, posZ, count, particles), new BlockPos(posX, posY, posZ), world);
  }

  private PacketSpawnParticles(double posX, double posY, double posZ, int count, EnumParticleTypes... particles) {
    this.posX = posX;
    this.posY = posY;
    this.posZ = posZ;
    this.count = count;
    this.particles = Arrays.asList(particles);
  }

  @Override
  public void toBytes(ByteBuf buffer) {
    buffer.writeFloat((float) posX);
    buffer.writeFloat((float) posY);
    buffer.writeFloat((float) posZ);
    buffer.writeByte(count);
    int size = particles.size();
    buffer.writeByte(size);
    for (EnumParticleTypes particle : particles) {
      buffer.writeByte(particle.ordinal());
    }

  }

  @Override
  public void fromBytes(ByteBuf buffer) {
    posX = buffer.readFloat();
    posY = buffer.readFloat();
    posZ = buffer.readFloat();
    count = buffer.readByte();
    int size = buffer.readByte();
    particles = new ArrayList<EnumParticleTypes>(size);
    for (int i = 0; i < size; i++) {
      particles.add(EnumParticleTypes.values()[buffer.readByte()]);
    }
  }

  public static class Handler implements IMessageHandler<PacketSpawnParticles, IMessage> {

    @Override
    public IMessage onMessage(PacketSpawnParticles message, MessageContext ctx) {
      for (EnumParticleTypes particle : message.particles) {
        if (particle != null) {
          ClientUtil.spawnParcticles(message.posX, message.posY, message.posZ, message.count, particle);
        }
      }
      return null;
    }
  }
}
