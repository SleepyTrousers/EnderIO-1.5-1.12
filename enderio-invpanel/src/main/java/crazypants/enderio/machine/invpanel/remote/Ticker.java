package crazypants.enderio.machine.invpanel.remote;

import crazypants.enderio.machine.invpanel.InventoryPanelContainer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.relauncher.Side;

public class Ticker {

  private static boolean registered = false;

  public static void create() {
    if (!registered) {
      MinecraftForge.EVENT_BUS.register(new Ticker());
      registered = true;
    }
  }

  private Ticker() {
  }

  @SubscribeEvent
  public void onPlayerTick(PlayerTickEvent event) {
    if (event.side == Side.SERVER && event.phase == Phase.END && event.player.openContainer instanceof InventoryPanelContainer) {
      ((InventoryPanelContainer) event.player.openContainer).tick(event.player);
    }
  }

}
