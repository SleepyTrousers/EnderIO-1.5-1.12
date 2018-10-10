package crazypants.enderio.base.handler;

import java.util.IdentityHashMap;
import java.util.Map.Entry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.util.NullHelper;
import com.google.common.collect.ImmutableSet;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.diagnostics.Prof;
import net.minecraft.profiler.Profiler;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

@EventBusSubscriber(modid = EnderIO.MODID)
public class ServerTickHandler {

  public interface ITickListener {
    void tickStart(TickEvent.ServerTickEvent event, @Nullable Profiler profiler);

    void tickEnd(TickEvent.ServerTickEvent event, @Nullable Profiler profiler);
  }

  private final static @Nonnull IdentityHashMap<ITickListener, String> listeners = new IdentityHashMap<>();

  public static void addListener(@Nonnull ITickListener listener) {
    listeners.put(listener, listener.getClass().getSimpleName());
  }

  public static void removeListener(@Nonnull ITickListener listener) {
    listeners.remove(listener);
  }

  public static void flush() {
    listeners.clear();
  }

  @SubscribeEvent
  public static void onServerTick(@Nonnull TickEvent.ServerTickEvent event) {
    Profiler profiler = FMLCommonHandler.instance().getMinecraftServerInstance().profiler;
    if (!profiler.profilingEnabled) {
      profiler = null;
    }
    Prof.start(profiler, "root"); // this event is fired outside the profiler's normal coverage...
    Prof.start(profiler, "ServerTickEvent_" + event.phase);
    for (Entry<ITickListener, String> entry : ImmutableSet.copyOf(NullHelper.notnullJ(listeners.entrySet(), "IdentityHashMap.entrySet()"))) {
      Prof.start(profiler, NullHelper.first(entry.getValue(), "(unnamed)"));
      if (event.phase == Phase.START) {
        entry.getKey().tickStart(event, profiler);
      } else {
        entry.getKey().tickEnd(event, profiler);
      }
      Prof.stop(profiler);
    }
    Prof.stop(profiler, 2);
  }

}
