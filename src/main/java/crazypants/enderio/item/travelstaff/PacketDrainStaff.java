package crazypants.enderio.item.travelstaff;

import crazypants.enderio.api.teleport.IItemOfTravel;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketDrainStaff implements IMessage {

  int powerUse;
  int hand;

  public PacketDrainStaff() {
  }

  public PacketDrainStaff(int powerUse, EnumHand hand) {
    this.powerUse = powerUse;
    this.hand = hand.ordinal();
  }

  @Override
  public void toBytes(ByteBuf buf) {
    buf.writeInt(powerUse);
    buf.writeInt(hand);
  }

  @Override
  public void fromBytes(ByteBuf buffer) {
    powerUse = buffer.readInt();
    hand = buffer.readInt();
  }

  public static class Handler implements IMessageHandler<PacketDrainStaff, IMessage> {

    @Override
    public IMessage onMessage(PacketDrainStaff message, MessageContext ctx) {
      EntityPlayer ep = ctx.getServerHandler().player;
      EnumHand theHand = EnumHand.values()[message.hand];
      if (theHand != null) {
        ItemStack heldItemMainhand = ep.getHeldItem(theHand);
        if (message.powerUse > 0 && heldItemMainhand.getItem() instanceof IItemOfTravel) {
          ItemStack item = heldItemMainhand.copy();
          ((IItemOfTravel) item.getItem()).extractInternal(item, message.powerUse);
          ep.setHeldItem(theHand, item);
        }
      }
      return null;
    }

  }

}
