package crazypants.enderio.base.scheduler;

import java.util.Calendar;
import java.util.Locale;

import javax.annotation.Nonnull;

import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;

public class Celeb implements Event {

  public static final Celeb H31 = new Celeb(2015, 10, 31, 0, 0, 2015, 11, 1, 0, 0);
  public static final Celeb C06 = new Celeb(2015, 12, 6, 0, 0, 2015, 12, 7, 0, 0);
  public static final Celeb C24 = new Celeb(2015, 12, 24, 12, 0, 2015, 12, 27, 0, 0);
  public static final Space SPACE = new Space(2017, 5, 1, 0, 0, 2017, 5, 1, 0, 0);

  public static void init(@Nonnull FMLPostInitializationEvent event) {
    Scheduler.instance.registerEvent(H31);
    Scheduler.instance.registerEvent(C06);
    Scheduler.instance.registerEvent(C24);
    Scheduler.instance.registerEvent(SPACE);
  }

  private boolean on = false;

  protected final Calendar start = Calendar.getInstance(Locale.getDefault());
  protected final Calendar end = Calendar.getInstance(Locale.getDefault());

  protected Celeb(int year0, int month0, int date0, int hourOfDay0, int minute0, int year1, int month1, int date1, int hourOfDay1, int minute1) {
    start.set(year0, month0 - 1, date0, hourOfDay0, minute0, 0);
    end.set(year1, month1 - 1, date1, hourOfDay1, minute1, 0);
  }

  @Override
  public boolean isActive(Calendar now) {
    if (start.before(now)) {
      if (end.before(now)) {
        calculate(now);
        on = false;
        return false;
      }
      return true;
    }
    return false;
  }

  @Override
  public long getTimeToStart(Calendar now) {
    long remaining = start.getTimeInMillis() - now.getTimeInMillis();
    return remaining < 0 ? 0 : remaining;
  }

  @Override
  public void calculate(Calendar now) {
    while (end.before(now)) {
      start.add(Calendar.YEAR, 1);
      end.add(Calendar.YEAR, 1);
    }
  }

  @Override
  public void run(Calendar now) {
    on = true;
  }

  @Override
  public String toString() {
    return "Celeb24 [start=" + start.toInstant() + ", end=" + end.toInstant() + "]";
  }

  public boolean isOn() {
    return on;
  }

}
