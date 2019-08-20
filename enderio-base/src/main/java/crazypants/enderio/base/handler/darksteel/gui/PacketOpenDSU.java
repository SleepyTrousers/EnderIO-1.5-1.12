package crazypants.enderio.base.handler.darksteel.gui;

import crazypants.enderio.base.init.ModObject;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketOpenDSU implements IMessage {

  int slot = -1;

  public PacketOpenDSU() {
  }

  public PacketOpenDSU(int slot) {
    this.slot = slot;
  }

  @Override
  public void toBytes(ByteBuf buf) {
    buf.writeShort(slot);
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    slot = buf.readShort();
  }

  public static class Handler implements IMessageHandler<PacketOpenDSU, IMessage> {

    @Override
    public IMessage onMessage(PacketOpenDSU message, MessageContext ctx) {
      EntityPlayer player = ctx.getServerHandler().player;
      ModObject.blockDarkSteelAnvil.openGui(player.world, player, message.slot, 0, 0);
      return null;
    }
  }
}
