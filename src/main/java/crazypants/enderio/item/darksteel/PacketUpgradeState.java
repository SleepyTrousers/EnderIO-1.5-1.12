package crazypants.enderio.item.darksteel;

import net.minecraft.entity.player.EntityPlayerMP;
import io.netty.buffer.ByteBuf;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class PacketUpgradeState implements IMessage, IMessageHandler<PacketUpgradeState, IMessage> {

  public enum Type {
    GLIDE,
    SPEED,
    STEP_ASSIST
  }
  
  public PacketUpgradeState() {    
  }
  
  private boolean isActive;
  private Type type;
  
  public PacketUpgradeState(Type type, boolean isActive) {
    this.type = type;
    this.isActive = isActive;
  }

  @Override
  public void toBytes(ByteBuf buf) {
    buf.writeShort(type.ordinal());
    buf.writeBoolean(isActive);    
  }
  
  @Override
  public void fromBytes(ByteBuf buf) {
    type = Type.values()[buf.readShort()];
    isActive = buf.readBoolean();    
  }
  
  @Override
  public IMessage onMessage(PacketUpgradeState message, MessageContext ctx) {
    EntityPlayerMP player = ctx.getServerHandler().playerEntity;
    switch(message.type) {
    case GLIDE:
      DarkSteelController.instance.setGlideActive(player, message.isActive);
      break;
    case SPEED:
      DarkSteelController.instance.setSpeedActive(player, message.isActive);
      break;
    case STEP_ASSIST:
      DarkSteelController.instance.setStepAssistActive(player, message.isActive);
      break;
    default:
      break;    
    }    
    return null;
  }

}
