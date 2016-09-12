package crazypants.util;

import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import crazypants.enderio.EnderIO;
import crazypants.enderio.Log;

public class Profiler {

  public static final Profiler instance = new Profiler();

  private static class Data {
    long time = 0, count = 0;
  }

  private ConcurrentHashMap<String, Data> profiler = new ConcurrentHashMap<String, Data>();
  private long lastProfiled = 0;
  private boolean on = false;

  public long start() {
    return on ? System.nanoTime() : Long.MAX_VALUE;
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
        profiler.putIfAbsent(source, new Data());
        Data data = profiler.get(source);
        data.time += elapsed;
        data.count++;
        if (EnderIO.proxy.getTickCount() > lastProfiled) {
          lastProfiled = EnderIO.proxy.getTickCount() + 200;
          for (Entry<String, Data> e : profiler.entrySet()) {
            long avg = e.getValue().time / e.getValue().count;
            Log.info(e.getKey() + ": " + avg + " ns avg over " + e.getValue().count + " calls");
          }
        }
      } catch (Throwable t) {
        // NOP
      }
    }
  }

}
