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

  public static class Data {
    final double posX, posY, posZ;
    final List<EnumParticleTypes> particles;
    final int count;

    public Data(double posX, double posY, double posZ, int count, EnumParticleTypes... particles) {
      this.posX = posX;
      this.posY = posY;
      this.posZ = posZ;
      this.particles = Arrays.asList(particles);
      this.count = count;
    }

    Data(ByteBuf buffer) {
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

    void toBytes(ByteBuf buffer) {
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

  }

  private final List<Data> data = new ArrayList<>();

  public PacketSpawnParticles() {
  }

  public static void create(Entity position, EnumParticleTypes... particles) {
    create(position, 5, particles);
  }

  public static void create(Entity position, int count, EnumParticleTypes... particles) {
    new PacketSpawnParticles().add(position.posX, position.posY + position.height * 0.8, position.posZ, count, particles).send(position.world,
        new BlockPos(position));
  }

  public static void create(World world, BlockPos position, EnumParticleTypes... particles) {
    create(world, position, 5, particles);
  }

  public static void create(World world, BlockPos position, int count, EnumParticleTypes... particles) {
    new PacketSpawnParticles().add(position.getX() + .5, position.getY() + .5, position.getZ() + .5, count, particles).send(world, position);
  }

  public static void create(World world, double posX, double posY, double posZ, int count, EnumParticleTypes... particles) {
    new PacketSpawnParticles().add(posX, posY, posZ, count, particles).send(world, new BlockPos(posX, posY, posZ));
  }

  public PacketSpawnParticles add(Entity position, int count, EnumParticleTypes... particles) {
    data.add(new Data(position.posX, position.posY + position.height * 0.8, position.posZ, count, particles));
    return this;
  }

  public PacketSpawnParticles add(BlockPos position, EnumParticleTypes... particles) {
    return add(position, 5, particles);
  }

  public PacketSpawnParticles add(BlockPos position, int count, EnumParticleTypes... particles) {
    data.add(new Data(position.getX() + .5, position.getY() + .5, position.getZ() + .5, count, particles));
    return this;
  }

  public PacketSpawnParticles add(double posX, double posY, double posZ, int count, EnumParticleTypes... particles) {
    data.add(new Data(posX, posY, posZ, count, particles));
    return this;
  }

  public void send(World world, BlockPos reference) {
    PacketHandler.INSTANCE.sendToAllAround(this, reference, world);
  }

  @Override
  public void toBytes(ByteBuf buffer) {
    buffer.writeInt(data.size());
    for (Data elem : data) {
      elem.toBytes(buffer);
    }
  }

  @Override
  public void fromBytes(ByteBuf buffer) {
    int count = buffer.readInt();
    for (int i = 0; i < count; i++) {
      data.add(new Data(buffer));
    }
  }

  public static class Handler implements IMessageHandler<PacketSpawnParticles, IMessage> {

    @Override
    public IMessage onMessage(PacketSpawnParticles message, MessageContext ctx) {
      for (Data data : message.data) {
        for (EnumParticleTypes particle : data.particles) {
          if (particle != null) {
            ClientUtil.spawnParcticles(data.posX, data.posY, data.posZ, data.count, particle);
          }
        }
      }
      return null;
    }
  }
}
