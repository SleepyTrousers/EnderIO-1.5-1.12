package crazypants.enderio.base.scheduler;

import java.util.Calendar;

public class Space extends Celeb {

  /*
   * International Space Day, see e.g. https://www.daysoftheyear.com/days/international-space-day/
   * 
   * In 2018 this coincides with Star Wars day, lol.
   */

  protected Space(int year0, int month0, int date0, int hourOfDay0, int minute0, int year1, int month1, int date1, int hourOfDay1, int minute1) {
    super(year0, month0, date0, hourOfDay0, minute0, year1, month1, date1, hourOfDay1, minute1);
  }

  @Override
  public void calculate(Calendar now) {
    // Yes, it's ugly.
    while (end.before(now)) {
      start.add(Calendar.YEAR, 1);
      start.set(Calendar.MONTH, Calendar.MAY);
      start.set(Calendar.DAY_OF_MONTH, 1);
      while (start.get(Calendar.DAY_OF_WEEK) != Calendar.FRIDAY) {
        start.add(Calendar.DAY_OF_MONTH, 1);
      }
      end.set(Calendar.YEAR, start.get(Calendar.YEAR));
      end.set(Calendar.MONTH, start.get(Calendar.MONTH));
      end.set(Calendar.DAY_OF_MONTH, start.get(Calendar.DAY_OF_MONTH));
      end.add(Calendar.DAY_OF_MONTH, 1);
    }
  }

}
