package crazypants.enderio.base.item.xptransfer;

import com.enderio.core.client.ClientUtil;

import crazypants.enderio.base.EnderIO;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketXpTransferEffects implements IMessage {

  boolean swing;
  long pos;

  public PacketXpTransferEffects(boolean swing, BlockPos pos) {
    this.swing = swing;
    this.pos = pos.toLong();
  }

  public PacketXpTransferEffects() {
  }

  @Override
  public void toBytes(ByteBuf buf) {
    buf.writeBoolean(swing);
    buf.writeLong(pos);
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    swing = buf.readBoolean();
    pos = buf.readLong();
  }

  public static class Handler implements IMessageHandler<PacketXpTransferEffects, IMessage> {

    @Override
    public IMessage onMessage(PacketXpTransferEffects message, MessageContext ctx) {
      EntityPlayer player = EnderIO.proxy.getClientPlayer();
      if (player != null) {
        int particleCount = 5;
        if (message.swing) {
          player.swingArm(EnumHand.MAIN_HAND);
          particleCount += 5;
        }

        BlockPos pos = BlockPos.fromLong(message.pos);

        for (int i = 0; i < particleCount; i++) {
          float xOffset = 0.1F - player.world.rand.nextFloat() * 1.2F;
          float yOffset = 0.1F - player.world.rand.nextFloat() * 1.2F;
          float zOffset = 0.1F - player.world.rand.nextFloat() * 1.2F;

          Particle fx = Minecraft.getMinecraft().effectRenderer.spawnEffectParticle(EnumParticleTypes.SPELL.getParticleID(), pos.getX() + xOffset,
              pos.getY() + yOffset, pos.getZ() + zOffset, 0.0D, 0.0D, 0.0D);
          if (fx != null) {
            fx.setRBGColorF(0.2f, 0.8f, 0.2f);
            ClientUtil.setParticleVelocityY(fx, ClientUtil.getParticleVelocityY(fx) * 0.5);
          }
        }

      }
      return null;
    }

  }

}
