package crazypants.enderio.diagnostics;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

public class DiagnosticsRegistry {

  // No ConcurrentHashSet available
  private static final ConcurrentHashMap<IDiagnosticsTracker, Boolean> trackers = new ConcurrentHashMap<IDiagnosticsTracker, Boolean>();

  public static void register(IDiagnosticsTracker tracker) {
    trackers.put(tracker, Boolean.TRUE);
  }

  public static void discard(IDiagnosticsTracker tracker) {
    trackers.remove(tracker);
  }

  public static Collection<IDiagnosticsTracker> getActiveTrackers() {
    return trackers.keySet();
  }

}
