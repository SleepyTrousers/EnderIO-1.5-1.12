package crazypants.enderio.base.item.conduitprobe;

import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import static crazypants.enderio.base.init.ModObject.itemConduitProbe;

public class PacketConduitProbeMode implements IMessage {

  public PacketConduitProbeMode() {
  }

  @Override
  public void toBytes(ByteBuf buf) {
  }

  @Override
  public void fromBytes(ByteBuf buf) {
  }

  public static class Handler implements IMessageHandler<PacketConduitProbeMode, IMessage> {

    @Override
    public IMessage onMessage(PacketConduitProbeMode message, MessageContext ctx) {
      ItemStack stack = ctx.getServerHandler().player.getHeldItemMainhand();
      if (stack.getItem() == itemConduitProbe.getItemNN()) {
        int newMeta = stack.getItemDamage() == 0 ? 1 : 0;
        stack.setItemDamage(newMeta);
        return null;
      }
      stack = ctx.getServerHandler().player.getHeldItemOffhand();
      if (stack.getItem() == itemConduitProbe.getItemNN()) {
        int newMeta = stack.getItemDamage() == 0 ? 1 : 0;
        stack.setItemDamage(newMeta);
        return null;
      }
      return null;
    }
  }
}
