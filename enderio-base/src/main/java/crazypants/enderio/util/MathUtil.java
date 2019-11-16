package crazypants.enderio.util;

import java.util.List;

public final class MathUtil {

  public static int cycle(int last, int min, int max) {
    int next = last + 1;
    if (next > max) {
      next = min;
    }
    return next;
  }

  public static int cycleReversed(int last, int min, int max) {
    int next = last - 1;
    if (next < min) {
      next = max;
    }
    return next;
  }

  public static int cycle(int last, List<?> bounds) {
    int next = last + 1;
    if (next >= bounds.size()) {
      next = 0;
    }
    return next;
  }

  public static int cycleReverse(int last, List<?> bounds) {
    int next = last - 1;
    if (next < 0) {
      next = bounds.size() - 1;
    }
    return next;
  }

  public static long clamp(long num, long min, long max) {
    if (num < min) {
      return min;
    } else {
      return num > max ? max : num;
    }
  }

}
