package crazypants.enderio.teleport.packet;

import crazypants.enderio.GuiID;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketOpenAuthGui implements IMessage, IMessageHandler<PacketOpenAuthGui, IMessage> {

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

  @Override
  public IMessage onMessage(PacketOpenAuthGui message, MessageContext ctx) {
    GuiID.GUI_ID_TRAVEL_AUTH.openGui(ctx.getServerHandler().playerEntity.worldObj, BlockPos.fromLong(message.pos),
        ctx.getServerHandler().playerEntity, null);
    return null;
  }
}
