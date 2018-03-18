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
  static final @Nonnull Map<String, ValueFactory> factories = new HashMap<>();

  public static void read(String mod, String section, final ByteBuf buf) {
    factories.get(mod + "." + section).read(buf);
  }

  static void registerFactory(@Nonnull ValueFactory factory) {
    synchronized (factories) {
      factories.put(factory.getModid() + "." + factory.getSection(), factory);
    }
  }

  @SubscribeEvent
  public void onPlayerLoggon(final PlayerLoggedInEvent evt) {
    for (ValueFactory factory : factories.values()) {
      PacketHandler.sendTo(new PacketConfigSyncNew(factory), (EntityPlayerMP) evt.player);
      Log.info("Sent config to player " + evt.player.getDisplayNameString() + " for " + factory.getModid() + " (" + factory.getSection() + ")");
    }
  }

  @SubscribeEvent
  public void onPlayerLogout(final ClientDisconnectionFromServerEvent event) {
    for (ValueFactory factory : factories.values()) {
      factory.endServerOverride();
      Log.info("Removed server config override for " + factory.getModid() + " (" + factory.getSection() + ")");
    }
  }

}
