package crazypants.enderio.machine.obelisk;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import crazypants.enderio.network.PacketHandler;
import crazypants.util.ClientUtil;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketObeliskFx implements IMessage, IMessageHandler<PacketObeliskFx, IMessage> {

  private double posX, posY, posZ;
  private List<EnumParticleTypes> particles;

  public PacketObeliskFx() {
  }

  public static void create(Entity position, EnumParticleTypes... particles) {
    PacketHandler.INSTANCE.sendToAllAround(new PacketObeliskFx(position, particles),
        new TargetPoint(position.world.provider.getDimension(), position.posX, position.posY, position.posZ, 64));
  }

  public PacketObeliskFx(Entity position, EnumParticleTypes... particles) {
    this.posX = position.posX;
    this.posY = position.posY + position.height * 0.8;
    this.posZ = position.posZ;
    this.particles = Arrays.asList(particles);
  }

  @Override
  public void toBytes(ByteBuf buffer) {
    buffer.writeFloat((float) posX);
    buffer.writeFloat((float) posY);
    buffer.writeFloat((float) posZ);
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
    int size = buffer.readByte();
    particles = new ArrayList<EnumParticleTypes>(size);
    for (int i = 0; i < size; i++) {
      particles.add(EnumParticleTypes.values()[buffer.readByte()]);
    }
  }

  @Override
  public IMessage onMessage(PacketObeliskFx message, MessageContext ctx) {
    for (EnumParticleTypes particle : message.particles) {
      ClientUtil.spawnParcticles(message.posX, message.posY, message.posZ, 5, particle);
    }
    return null;
  }
}
