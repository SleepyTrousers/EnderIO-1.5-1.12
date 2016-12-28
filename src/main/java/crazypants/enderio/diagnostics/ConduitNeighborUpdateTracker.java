package crazypants.enderio.diagnostics;

import java.util.ArrayList;
import java.util.List;

public class ConduitNeighborUpdateTracker implements IDiagnosticsTracker {

  private final String activityDescription;
  private final List<Object> poses = new ArrayList<Object>();
  private final List<Long> times = new ArrayList<Long>();
  private boolean running = false;

  public ConduitNeighborUpdateTracker(String activityDescription) {
    this.activityDescription = activityDescription;
    DiagnosticsRegistry.register(this);
  }

  @Override
  public String getActivityDescription() {
    return activityDescription;
  }

  @Override
  public List<String> getLines() {
    if (running) {
      stop();
    }
    List<String> result = new ArrayList<String>();
    for (int i = 0; i < poses.size(); i++) {
      Object pos = poses.get(i);
      Long time = times.get(i);
      result.add(pos + " took " + (time == 0L ? "??? " : time) + "ns");
    }
    if (running) {
      result.add("The last entry contains the time to process the crash and will always be significantly higher.");
    }
    return result;
  }

  @Override
  public void start(Object on) {
    running = true;
    poses.add(on);
    times.add(System.nanoTime());
  }

  @Override
  public void stop() {
    long end = System.nanoTime();
    int i = times.size() - 1;
    times.set(i, end - times.get(i));
    running = false;
  }

  @Override
  public void discard() {
    DiagnosticsRegistry.discard(this);
  }

}
