package crazypants.enderio.base.init;

import javax.annotation.Nonnull;

import crazypants.enderio.base.EnderIO;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;

@EventBusSubscriber(modid = EnderIO.MODID)
public final class TickTimer {

  private static long serverTickCount = 0;
  private static long clientTickCount = 0;
  private static long clientPausedTickCount = 0;

  @SubscribeEvent
  public static void onTick(@Nonnull ServerTickEvent evt) {
    if (evt.phase == Phase.END) {
      ++serverTickCount;
    }
  }

  @SubscribeEvent
  public static void onTick(@Nonnull ClientTickEvent evt) {
    if (evt.phase == Phase.END) {
      if (Minecraft.getMinecraft().isGamePaused()) {
        ++clientPausedTickCount;
      } else {
        ++clientTickCount;
      }
    }
  }

  public static long getServerTickCount() {
    return serverTickCount;
  }

  public static long getClientTickCount() {
    return clientTickCount;
  }

  public static long getClientPausedTickCount() {
    return clientPausedTickCount;
  }
}