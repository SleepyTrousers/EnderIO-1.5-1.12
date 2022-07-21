package crazypants.enderio.machine.transceiver;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;
import crazypants.enderio.network.PacketHandler;
import net.minecraft.entity.player.EntityPlayerMP;

public class ConnectionHandler {

    @SubscribeEvent
    public void onPlayerLoggon(PlayerLoggedInEvent evt) {
        PacketHandler.INSTANCE.sendTo(
                new PacketChannelList(ServerChannelRegister.instance), (EntityPlayerMP) evt.player);
    }

    @SubscribeEvent
    public void onDisconnectedFromServer(ClientDisconnectionFromServerEvent evt) {
        ClientChannelRegister.instance.reset();
    }
}
