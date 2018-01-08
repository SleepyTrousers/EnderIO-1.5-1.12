package info.loenwind.scheduler;

import java.util.Calendar;

public interface Event {

  boolean isActive(Calendar now);

  long getTimeToStart(Calendar now);

  void calculate(Calendar now);

  void run(Calendar now);

}
