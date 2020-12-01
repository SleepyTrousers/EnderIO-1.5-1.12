package crazypants.enderio.util;

import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;

import com.enderio.core.common.util.NNList;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.Log;

public class Profiler {

  public static final Profiler instance = new Profiler(false);

  private static class Data {
    long time = 0, count = 0;
  }

  private ConcurrentHashMap<String, Data> profiler = new ConcurrentHashMap<String, Data>();
  private long lastProfiled = 0;
  private final boolean on;

  public Profiler(boolean on) {
    this.on = on;
  }

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
          NNList<String> keys = new NNList<>(profiler.keySet());
          Collections.sort(keys, (a, b) -> a.compareTo(b));
          for (String key : keys) {
            Data value = profiler.get(key);
            long avg = value.time / value.count;
            Log.info(key + ": " + avg + " ns avg over " + value.count + " calls");
          }
        }
      } catch (Throwable t) {
        // NOP
      }
    }
  }

}
