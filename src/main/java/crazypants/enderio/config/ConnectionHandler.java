package crazypants.enderio.config;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import crazypants.enderio.network.PacketHandler;
import net.minecraft.entity.player.EntityPlayerMP;

public class ConnectionHandler
{
  private final MessageConfigSync message;
  public ConnectionHandler(MessageConfigSync message)
  {
    this.message = message;
  }
  
  @SubscribeEvent
  public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent evt)
  {
    PacketHandler.INSTANCE.sendTo(message, (EntityPlayerMP)evt.player);
  }
}
