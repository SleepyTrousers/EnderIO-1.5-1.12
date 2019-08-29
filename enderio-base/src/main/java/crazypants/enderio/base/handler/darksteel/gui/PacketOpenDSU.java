package crazypants.enderio.base.handler.darksteel.gui;

import javax.annotation.Nullable;

import crazypants.enderio.base.init.ModObject;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
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
  public void toBytes(@Nullable ByteBuf buf) {
    if (buf != null) {
      buf.writeShort(slot);
    }
  }

  @Override
  public void fromBytes(@Nullable ByteBuf buf) {
    if (buf != null) {
      slot = buf.readShort();
    }
  }

  public static class Handler implements IMessageHandler<PacketOpenDSU, IMessage> {

    @Override
    public @Nullable IMessage onMessage(@Nullable PacketOpenDSU message, @Nullable MessageContext ctx) {
      if (message != null && ctx != null) {
        EntityPlayer player = ctx.getServerHandler().player;
        ModObject.blockDarkSteelAnvil.openGui(player.world, new BlockPos(0, -1, 0), player, null, message.slot);
      }
      return null;
    }
  }
}
