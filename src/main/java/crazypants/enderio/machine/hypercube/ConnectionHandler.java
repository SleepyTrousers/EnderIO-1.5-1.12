package crazypants.enderio.machine.hypercube;

import crazypants.enderio.network.PacketHandler;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;

public class ConnectionHandler {

  //to lazy to create a new class
  @SubscribeEvent
  public void onPlayerLoggon(PlayerLoggedInEvent evt) {
    PacketHandler.INSTANCE.sendTo(new PacketChannelList(evt.player, true), (EntityPlayerMP) evt.player);
    PacketHandler.INSTANCE.sendTo(new PacketChannelList(evt.player, false), (EntityPlayerMP) evt.player);
  }

  @SubscribeEvent
  public void onDisconnectedFromServer(ClientDisconnectionFromServerEvent evt) {    
    ClientChannelRegister.instance.reset();
  }

}
