package crazypants.enderio.teleport.packet;

import crazypants.enderio.api.teleport.IItemOfTravel;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

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
    if(message.powerUse > 0 && ep.getHeldItemMainhand() != null && ep.getHeldItemMainhand().getItem() instanceof IItemOfTravel) {
      ItemStack item = ep.getHeldItemMainhand().copy();
      ((IItemOfTravel) item.getItem()).extractInternal(item, message.powerUse);
      ep.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, item);      
    }
    return null;
  }

}
