package crazypants.enderio.base.scheduler;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public final class Scheduler extends Thread {

  public final static Scheduler instance = new Scheduler();

  private final List<Event> events = new ArrayList<Event>();

  public void registerEvent(Event event) {
    if (this.isAlive()) {
      throw new RuntimeException("Bad state");
    }
    event.calculate(Calendar.getInstance(Locale.getDefault()));
    events.add(event);
  }

  private Scheduler() {
    super("Ender IO Scheduler");
  }

  @Override
  public void run() {
    while (!events.isEmpty()) {
      try {
        final Calendar cal = Calendar.getInstance(Locale.getDefault());
        for (Event event : events) {
          if (event.isActive(cal)) {
            event.run(cal);
          }
        }
        Collections.sort(events, new Sorter(cal));
        long tts = events.get(0).getTimeToStart(cal);
        if (tts > 0) {
          sleep(tts);
        } else {
          if (tts < 0) {
            events.remove(0);
          }
          sleep(1000);
        }
      } catch (InterruptedException e) {
      }
    }
  }

  private static class Sorter implements Comparator<Event> {

    private final Calendar cal;

    Sorter(Calendar cal) {
      this.cal = cal;
    }

    @Override
    public int compare(Event o1, Event o2) {
      long tts1 = o1.getTimeToStart(cal);
      long tts2 = o2.getTimeToStart(cal);
      return Long.compare(tts1, tts2);
    }

  }

}
