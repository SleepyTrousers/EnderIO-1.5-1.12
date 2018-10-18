package crazypants.enderio.machines.darksteel.upgrade.wet;

import crazypants.enderio.base.EnderIO;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketBlockTransformFXPacket implements IMessage {

  public BlockPos position;

  public PacketBlockTransformFXPacket() {

  }

  public PacketBlockTransformFXPacket(BlockPos position) {
    this.position = position;
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    position = new BlockPos(buf.readDouble(), buf.readDouble(), buf.readDouble());
  }

  @Override
  public void toBytes(ByteBuf buf) {
    buf.writeDouble(position.getX());
    buf.writeDouble(position.getY());
    buf.writeDouble(position.getZ());
  }

  public static class Handler implements IMessageHandler<PacketBlockTransformFXPacket, IMessage> {

    @Override
    public IMessage onMessage(PacketBlockTransformFXPacket message, MessageContext ctx) {
      WetUpgrade.INSTANCE.triggerLiquidConversionEffects(EnderIO.proxy.getClientWorld(), message.position);
      return null;
    }

  }

}
