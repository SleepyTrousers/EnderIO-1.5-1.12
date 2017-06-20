package crazypants.enderio.machine.transceiver;

import crazypants.enderio.network.PacketHandler;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;

public class ConnectionHandler {

  @SubscribeEvent
  public void onPlayerLoggon(PlayerLoggedInEvent evt) {
    PacketHandler.INSTANCE.sendTo(new PacketChannelList(ServerChannelRegister.instance), (EntityPlayerMP) evt.player);
  }

  @SubscribeEvent
  public void onDisconnectedFromServer(ClientDisconnectionFromServerEvent evt) {
    ClientChannelRegister.instance.reset();
  }

}
