package crazypants.enderio.machine.obelisk.xp;

import com.enderio.core.client.ClientUtil;

import crazypants.enderio.EnderIO;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketXpTransferEffects implements IMessage, IMessageHandler<PacketXpTransferEffects, IMessage> {

  boolean swing;
  double x;
  double y;
  double z;

  public PacketXpTransferEffects(boolean swing, double x, double y, double z) {
    this.swing = swing;
    this.x = x;
    this.y = y;
    this.z = z;
  }

  public PacketXpTransferEffects() {
  }

  @Override
  public void toBytes(ByteBuf buf) {
    buf.writeBoolean(swing);
    buf.writeDouble(x);
    buf.writeDouble(y);
    buf.writeDouble(z);
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    swing = buf.readBoolean();
    x = buf.readDouble();
    y = buf.readDouble();
    z = buf.readDouble();
  }

  @Override
  public IMessage onMessage(PacketXpTransferEffects message, MessageContext ctx) {
    EntityPlayer player = EnderIO.proxy.getClientPlayer();
    if (player != null) {
      int particleCount = 1;
      if (message.swing) {
        player.swingArm(EnumHand.MAIN_HAND);
        particleCount = 5;
      }

      for (int i = 0; i < particleCount; i++) {
        float xOffset = 0.1F - player.world.rand.nextFloat() * 0.2F;
        float yOffset = 0.1F - player.world.rand.nextFloat() * 0.2F;
        float zOffset = 0.1F - player.world.rand.nextFloat() * 0.2F;

        Particle fx = Minecraft.getMinecraft().effectRenderer.spawnEffectParticle(EnumParticleTypes.SPELL.getParticleID(), message.x + xOffset, message.y
            + yOffset, message.z + zOffset, 0.0D, 0.0D, 0.0D);
        if (fx != null) {
          fx.setRBGColorF(0.2f, 0.8f, 0.2f);
          ClientUtil.setParticleVelocityY(fx, ClientUtil.getParticleVelocityY(fx) * 0.5);
        }
      }

    }
    return null;
  }

}
