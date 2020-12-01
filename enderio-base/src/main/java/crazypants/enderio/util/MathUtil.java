package crazypants.enderio.util;

import java.util.List;

import com.google.common.math.BigIntegerMath;

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

  public static boolean isAny(int valueToTest, int value1, int value2) {
    return valueToTest == value1 || valueToTest == value2;
  }

  public static boolean isAny(int valueToTest, int value1, int value2, int value3) {
    return valueToTest == value1 || valueToTest == value2 || valueToTest == value3;
  }

  public static boolean isAny(int valueToTest, int value1, int value2, int value3, int value4) {
    return valueToTest == value1 || valueToTest == value2 || valueToTest == value3 || valueToTest == value4;
  }

  public static boolean isAny(long valueToTest, long value1, long value2) {
    return valueToTest == value1 || valueToTest == value2;
  }

  public static boolean isAny(long valueToTest, long value1, long value2, long value3) {
    return valueToTest == value1 || valueToTest == value2 || valueToTest == value3;
  }

  public static boolean isAny(long valueToTest, long value1, long value2, long value3, long value4) {
    return valueToTest == value1 || valueToTest == value2 || valueToTest == value3 || valueToTest == value4;
  }

  public static long fac(int l) {
    return BigIntegerMath.factorial(l).longValue();
  }

  public static long termial(long level) {
    // ∑ 0 ... level
    return (level * level + level) / 2l;
  }

  public static int limit(long l) {
    // assert(l > 0);
    return (l & 0xFFFFFFFF80000000L) != 0 ? Integer.MAX_VALUE : (int) l;
  }

}
