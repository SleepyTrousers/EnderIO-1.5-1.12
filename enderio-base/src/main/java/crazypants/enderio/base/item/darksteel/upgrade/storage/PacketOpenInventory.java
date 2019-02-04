package crazypants.enderio.base.item.darksteel.upgrade.storage;

import crazypants.enderio.base.init.ModObject;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketOpenInventory implements IMessage {

  public PacketOpenInventory() {
  }

  @Override
  public void toBytes(ByteBuf buf) {
  }

  @Override
  public void fromBytes(ByteBuf buf) {
  }

  public static class Handler implements IMessageHandler<PacketOpenInventory, IMessage> {

    @Override
    public IMessage onMessage(PacketOpenInventory message, MessageContext ctx) {
      EntityPlayer player = ctx.getServerHandler().player;
      ModObject.itemDarkSteelChestplate.openGui(player.world, player, 0, 0, 0);
      return null;
    }
  }
}
