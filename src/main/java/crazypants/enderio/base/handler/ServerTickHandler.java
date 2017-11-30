package crazypants.enderio.base.handler;

import java.util.IdentityHashMap;
import java.util.Map.Entry;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.base.EnderIO;
import net.minecraft.profiler.Profiler;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

@EventBusSubscriber(modid = EnderIO.MODID)
public class ServerTickHandler {

  public static interface ITickListener {
    public void tickStart(TickEvent.ServerTickEvent event, Profiler profiler);

    public void tickEnd(TickEvent.ServerTickEvent event, Profiler profiler);
  }

  private final static @Nonnull IdentityHashMap<ITickListener, String> listeners = new IdentityHashMap<>();

  public static void addListener(@Nonnull ITickListener listener) {
    listeners.put(listener, listener.getClass().getName().replaceFirst(".*\\.", ""));
  }

  public static void removeListener(@Nonnull ITickListener listener) {
    listeners.remove(listener);
  }

  public static void flush() {
    listeners.clear();
  }

  @SubscribeEvent
  public static void onServerTick(@Nonnull TickEvent.ServerTickEvent event) {
    final Profiler profiler = FMLCommonHandler.instance().getMinecraftServerInstance().profiler;
    profiler.startSection("root"); // this event is fired outside the profiler's normal coverage...
    profiler.startSection("ServerTickEvent");
    for (Entry<ITickListener, String> entry : listeners.entrySet()) {
      profiler.startSection(NullHelper.first(entry.getValue(), "(unnamed)"));
      if (event.phase == Phase.START) {
        entry.getKey().tickStart(event, profiler);
      } else {
        entry.getKey().tickEnd(event, profiler);
      }
      profiler.endSection();
    }
    profiler.endSection(); // ServerTickEvent
    profiler.endSection(); // root
  }

}
