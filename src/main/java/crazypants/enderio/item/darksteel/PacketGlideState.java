package crazypants.enderio.item.darksteel;

import net.minecraft.entity.player.EntityPlayerMP;
import io.netty.buffer.ByteBuf;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class PacketGlideState implements IMessage, IMessageHandler<PacketGlideState, IMessage> {

  public PacketGlideState() {    
  }
  
  private boolean glideActive;
  
  public PacketGlideState(boolean glideActive) {
    this.glideActive = glideActive;
  }

  @Override
  public void toBytes(ByteBuf buf) {
    buf.writeBoolean(glideActive);
    
  }
  
  @Override
  public void fromBytes(ByteBuf buf) {
    glideActive = buf.readBoolean();    
  }
  
  @Override
  public IMessage onMessage(PacketGlideState message, MessageContext ctx) {
    EntityPlayerMP player = ctx.getServerHandler().playerEntity;
    DarkSteelController.instance.setGlideActive(player, message.glideActive);
    return null;
  }

}
