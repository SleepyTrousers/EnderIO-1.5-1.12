package crazypants.util;

import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import crazypants.enderio.EnderIO;
import crazypants.enderio.Log;

public class Profiler {

  public static final Profiler client = new Profiler();
  public static final Profiler server = client;

  private ConcurrentHashMap<String, Long> profiler = new ConcurrentHashMap<String, Long>();
  private ConcurrentHashMap<String, Long> profilerC = new ConcurrentHashMap<String, Long>();
  private long lastProfiled = 0;
  private boolean on = false;

  public long start() {
    //on = /* info.loenwind.enderioaddons.config.Config.profilingEnabled.getBoolean() && */EnderIO.proxy.getTickCount() > 20 * 30;
    return on ? System.nanoTime() : Long.MAX_VALUE;
  }

  public long start_always() {
    on = true;
    return System.nanoTime();
  }

  public long pause(long start) {
    return on ? System.nanoTime() - start : Long.MAX_VALUE;
  }

  public long resume(long start) {
    return on ? System.nanoTime() - start : Long.MAX_VALUE;
  }

  public void stop(long start, String source) {
    long elapsed = on ? System.nanoTime() - start : -1;
    if (elapsed >= 0) {
      try {
        profiler.putIfAbsent(source, 0L);
        profilerC.putIfAbsent(source, 0L);
        profiler.put(source, profiler.get(source) + elapsed);
        profilerC.put(source, profilerC.get(source) + 1);
        if (EnderIO.proxy.getTickCount() > lastProfiled) {
          lastProfiled = EnderIO.proxy.getTickCount() + 200;
          for (Entry<String, Long> e : profiler.entrySet()) {
            long avg = e.getValue() / profilerC.get(e.getKey());
            Log.info(e.getKey() + ": " + avg + " ns avg over " + profilerC.get(e.getKey()) + " calls");
          }
        }
      } catch (Throwable t) {
        // NOP
      }
    }
  }

}
