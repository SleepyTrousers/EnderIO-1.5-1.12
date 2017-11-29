package crazypants.enderio.base.teleport.packet;

import crazypants.enderio.base.GuiID;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketOpenAuthGui implements IMessage {

  long pos;

  public PacketOpenAuthGui() {
  }

  public PacketOpenAuthGui(BlockPos pos) {
    this.pos = pos.toLong();
  }

  @Override
  public void toBytes(ByteBuf buf) {
    buf.writeLong(pos);
  }

  @Override
  public void fromBytes(ByteBuf buffer) {
    pos = buffer.readLong();
  }

  public static class Handler implements IMessageHandler<PacketOpenAuthGui, IMessage> {

    @Override
    public IMessage onMessage(PacketOpenAuthGui message, MessageContext ctx) {
      GuiID.GUI_ID_TRAVEL_AUTH.openGui(ctx.getServerHandler().player.world, BlockPos.fromLong(message.pos), ctx.getServerHandler().player, null);
      return null;
    }

  }

}
