package crazypants.enderio.teleport;

import net.minecraft.entity.player.EntityPlayerMP;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import crazypants.enderio.EnderIO;
import crazypants.enderio.teleport.packet.PacketConfigSync;

public class ConnectionHandler {

  @SubscribeEvent
  public void onPlayerLoggon(PlayerLoggedInEvent evt) {
    EnderIO.packetPipeline.sendTo(new PacketConfigSync(), (EntityPlayerMP) evt.player);
  }

}
