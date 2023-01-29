package crazypants.enderio.machine.hypercube;

import net.minecraft.entity.player.EntityPlayerMP;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;
import crazypants.enderio.network.PacketHandler;

public class ConnectionHandler {

    // to lazy to create a new class
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
