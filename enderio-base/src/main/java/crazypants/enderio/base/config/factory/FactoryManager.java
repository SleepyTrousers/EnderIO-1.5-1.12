package crazypants.enderio.base.config.factory;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.Log;
import crazypants.enderio.base.network.PacketHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;

@EventBusSubscriber(modid = EnderIO.MODID)
public class FactoryManager {

  public static final @Nonnull String SERVER_OVERRIDE = " (synced from server)";
  private static final @Nonnull Map<String, IValueFactory> factories = new HashMap<>();

  static void read(String mod, String section, final ByteBuf buf) {
    Log.debug("Read " + factories.get(mod + "." + section).read(buf) + " config values from server packet for " + mod + " (" + section + ")");
  }

  static void registerFactory(@Nonnull IValueFactory factory) {
    synchronized (factories) {
      factories.put(factory.getModid() + "." + factory.getSection(), factory);
    }
  }

  @SubscribeEvent
  public static void onPlayerLoggon(final PlayerLoggedInEvent evt) {
    for (IValueFactory factory : factories.values()) {
      if (factory.needsSyncing()) {
        PacketHandler.sendTo(new PacketConfigSyncNew(factory), (EntityPlayerMP) evt.player);
        Log.debug("Sent config to player " + evt.player.getDisplayNameString() + " for " + factory.getModid() + " (" + factory.getSection() + ")");
      }
    }
  }

  @SubscribeEvent
  public static void onPlayerLogout(final ClientDisconnectionFromServerEvent event) {
    for (IValueFactory factory : factories.values()) {
      factory.endServerOverride();
      Log.debug("Removed server config override for " + factory.getModid() + " (" + factory.getSection() + ")");
    }
  }

}
