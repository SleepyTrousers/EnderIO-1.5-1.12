package crazypants.enderio.conduit.packet;

import crazypants.enderio.conduit.gui.ExternalConnectionContainer;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketSlotVisibility implements IMessage {

  private boolean inputVisible;
  private boolean outputVisible;

  public PacketSlotVisibility() {
  }

  public PacketSlotVisibility(boolean inputVisible, boolean outputVisible) {
    this.inputVisible = inputVisible;
    this.outputVisible = outputVisible;
  }

  @Override
  public void fromBytes(ByteBuf bb) {
    int value = bb.readUnsignedByte();
    inputVisible = (value & 1) != 0;
    outputVisible = (value & 2) != 0;
  }

  @Override
  public void toBytes(ByteBuf bb) {
    int value = (inputVisible ? 1 : 0) | (outputVisible ? 2 : 0);
    bb.writeByte(value);
  }

  public static class Handler implements IMessageHandler<PacketSlotVisibility, IMessage> {

    @Override
    public IMessage onMessage(PacketSlotVisibility message, MessageContext ctx) {
      EntityPlayerMP player = ctx.getServerHandler().player;
      if (player.openContainer instanceof ExternalConnectionContainer) {
        ExternalConnectionContainer ecc = (ExternalConnectionContainer) player.openContainer;
        ecc.setInOutSlotsVisible(message.inputVisible, message.outputVisible);
      }
      return null;
    }
  }
}
