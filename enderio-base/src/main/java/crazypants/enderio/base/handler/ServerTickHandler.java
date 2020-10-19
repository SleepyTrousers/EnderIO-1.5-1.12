package crazypants.enderio.base.handler;

import java.util.IdentityHashMap;
import java.util.Map.Entry;
import java.util.WeakHashMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.util.NullHelper;
import com.google.common.collect.ImmutableSet;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.config.config.DiagnosticsConfig;
import crazypants.enderio.base.diagnostics.Prof;
import net.minecraft.profiler.Profiler;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

@EventBusSubscriber(modid = EnderIO.MODID)
public class ServerTickHandler {

  @Deprecated
  public interface ITickListener {
    void tickStart(TickEvent.ServerTickEvent event, @Nullable Profiler profiler);

    void tickEnd(TickEvent.ServerTickEvent event, @Nullable Profiler profiler);
  }

  public interface IServerTickListener {
    default void tickStart(@Nullable Profiler profiler) {
    }

    default void tickEnd(@Nullable Profiler profiler) {
    };
  }

  private final static @Nonnull IdentityHashMap<ITickListener, String> listeners = new IdentityHashMap<>();

  private final static @Nonnull WeakHashMap<WorldServer, IdentityHashMap<IServerTickListener, String>> worldListeners = new WeakHashMap<>();

  @Deprecated
  public static void addListener(@Nonnull ITickListener listener) {
    listeners.put(listener, listener.getClass().getSimpleName());
  }

  public static void addListener(@Nonnull World world, @Nonnull IServerTickListener listener) {
    if (world instanceof WorldServer) {
      addListener((WorldServer) world, listener);
    }
  }

  public static void addListener(@Nonnull WorldServer world, @Nonnull IServerTickListener listener) {
    worldListeners.computeIfAbsent(world, k -> new IdentityHashMap<>()).put(listener, listener.getClass().getSimpleName());
  }

  public static void removeListener(@Nonnull Object listener) {
    listeners.remove(listener);
    worldListeners.forEach((k, v) -> v.remove(listener));
  }

  public static void flush() {
    listeners.clear();
    worldListeners.clear();
  }

  @SubscribeEvent
  public static void onWorldTick(@Nonnull TickEvent.WorldTickEvent event) {
    final Profiler profiler = event.world.profiler.profilingEnabled ? event.world.profiler : null;
    Prof.start(profiler, "WorldTickEvent_" + event.world.provider.getDimension() + "_" + event.phase);
    worldListeners.computeIfAbsent((WorldServer) event.world, k -> new IdentityHashMap<>()).forEach((listener, name) -> {
      Prof.start(profiler, NullHelper.first(name, "(unnamed)"));
      if (event.phase == Phase.START) {
        listener.tickStart(profiler);
      } else {
        listener.tickEnd(profiler);
      }
      Prof.stop(profiler);
    });
    Prof.stop(profiler);
  }

  @SubscribeEvent
  public static void onServerTick(@Nonnull TickEvent.ServerTickEvent event) {
    Profiler profiler = FMLCommonHandler.instance().getMinecraftServerInstance().profiler;
    if (!profiler.profilingEnabled) {
      profiler = null;
    } else if (DiagnosticsConfig.debugProfilerResetOnServerTick.get()) {
      // The profiler should be empty here, we are outside the area profiled by vanilla code.
      // Most likely cause for this is that there's a mod overflowing the profiler.
      // Reset it, so at least our data in here is recorded properly...
      String lastSection = profiler.getNameOfLastSection();
      while (!"[UNKNOWN]".equals(lastSection)) {
        profiler.endSection();
        lastSection = profiler.getNameOfLastSection();
      }
    }
    Prof.start(profiler, "root"); // profiler needs a "root" element to record anything
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
