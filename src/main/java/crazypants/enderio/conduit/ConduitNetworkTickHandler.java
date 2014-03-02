package crazypants.enderio.conduit;

import java.util.ArrayList;
import java.util.List;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;

public class ConduitNetworkTickHandler {

  public static final ConduitNetworkTickHandler instance = new ConduitNetworkTickHandler();

  public static interface TickListener {
    public void tickStart(TickEvent.ServerTickEvent evt);

    public void tickEnd(TickEvent.ServerTickEvent evt);
  }

  private final List<TickListener> listeners = new ArrayList<TickListener>();

  public void addListener(TickListener listener) {
    listeners.add(listener);
  }

  public void removeListener(TickListener listener) {
    listeners.remove(listener);
  }

  @SubscribeEvent
  public void onServerTick(TickEvent.ServerTickEvent event) {
    if(event.phase == Phase.START) {
      tickStart(event);
    } else {
      tickEnd(event);
    }
  }

  public void tickStart(TickEvent.ServerTickEvent event) {
    for (TickListener h : listeners) {
      h.tickStart(event);
    }
  }

  public void tickEnd(TickEvent.ServerTickEvent event) {
    for (TickListener h : listeners) {
      h.tickEnd(event);
    }
    listeners.clear();
  }

}
