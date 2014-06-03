package crazypants.enderio.teleport.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import crazypants.enderio.EnderIO;
import crazypants.enderio.teleport.ItemTravelStaff;

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
  
    if(ItemTravelStaff.isEquipped(ep)) {
      EnderIO.itemTravelStaff.extractInternal(ep.getCurrentEquippedItem(), message.powerUse);
    }
    return null;
  }

}
