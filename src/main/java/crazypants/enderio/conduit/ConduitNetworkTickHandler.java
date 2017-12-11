package crazypants.enderio.conduit;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;

import net.minecraft.profiler.Profiler;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

public class ConduitNetworkTickHandler {

  public static final ConduitNetworkTickHandler instance = new ConduitNetworkTickHandler();

  public static interface TickListener {
    public void tickStart(TickEvent.ServerTickEvent evt);

    public void tickEnd(TickEvent.ServerTickEvent evt, Profiler theProfiler);
  }

  private final List<TickListener> listeners = new ArrayList<TickListener>();

  private final IdentityHashMap<AbstractConduitNetwork<?,?>, Boolean> networks =
          new IdentityHashMap<AbstractConduitNetwork<?, ?>, Boolean>();

  public void addListener(TickListener listener) {
    listeners.add(listener);
  }

  public void removeListener(TickListener listener) {
    listeners.remove(listener);
  }

  public void registerNetwork(AbstractConduitNetwork<?,?> cn) {
    networks.put(cn, Boolean.TRUE);
  }

  public void unregisterNetwork(AbstractConduitNetwork<?,?> cn) {
    networks.remove(cn);
  }
  
  public void flush() {
    listeners.clear();
    networks.clear();
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
    final Profiler theProfiler = FMLCommonHandler.instance().getMinecraftServerInstance().profiler;
    theProfiler.startSection("root"); // this event is fired outside the profilers normal coverage...
    theProfiler.startSection("EnderIO_All_Conduit_Networks_Tick");
    for (TickListener h : listeners) {
      theProfiler.startSection(h.getClass().getName().replaceFirst(".*\\.", ""));
      h.tickEnd(event, theProfiler);
      theProfiler.endSection();
    }
    for(AbstractConduitNetwork<?,?> cn : networks.keySet()) {
      theProfiler.startSection(cn.getClass().getName().replaceFirst(".*\\.", ""));
      cn.doNetworkTick(theProfiler);
      theProfiler.endSection();
    }
    theProfiler.endSection();
    theProfiler.endSection();
  }

}
