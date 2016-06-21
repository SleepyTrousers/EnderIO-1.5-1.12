package crazypants.enderio.teleport.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import crazypants.enderio.Log;
import crazypants.enderio.api.teleport.IItemOfTravel;

public class PacketDrainStaff implements IMessage, IMessageHandler<PacketDrainStaff, IMessage> {

  int powerUse;

  public PacketDrainStaff() {
  }

  public PacketDrainStaff(int powerUse) {
    this.powerUse = powerUse;
  }

  @Override
  public void toBytes(ByteBuf buf) {
    buf.writeInt(powerUse);
  }

  @Override
  public void fromBytes(ByteBuf buffer) {
    powerUse = buffer.readInt();
  }

  @Override
  public IMessage onMessage(PacketDrainStaff message, MessageContext ctx) {
    EntityPlayer ep = ctx.getServerHandler().playerEntity;
    if(message.powerUse > 0 && ep.getCurrentEquippedItem() != null && ep.getCurrentEquippedItem().getItem() instanceof IItemOfTravel) {
      ItemStack item = ep.getCurrentEquippedItem().copy();
      ((IItemOfTravel) item.getItem()).extractInternal(item, message.powerUse);
      ep.setCurrentItemOrArmor(0, item);
    }
    return null;
  }

}
