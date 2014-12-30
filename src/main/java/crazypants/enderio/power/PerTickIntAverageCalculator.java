package crazypants.enderio.power;

import java.util.LinkedList;

public class PerTickIntAverageCalculator {

  private float lastSecondTotal = 0;
  private int index = 0;
  private final LimitedQueue<Float> secondsCache;

  public PerTickIntAverageCalculator() {
    this(5);
  }

  public PerTickIntAverageCalculator(int numSeconds) {
    secondsCache = new LimitedQueue<Float>(numSeconds);
  }

  public float getAverage() {
    int numTicks = index + (secondsCache.size() * 20);
    if(numTicks == 0) {
      return 0;
    }
    float totalPower = lastSecondTotal;
    for (Float fl : secondsCache) {
      totalPower += fl;
    }
    return totalPower / numTicks;
  }

  public void tick(int value) {
    lastSecondTotal += value;
    index++;
    if(index == 20) {
      secondsCache.add(lastSecondTotal);
      lastSecondTotal = 0;
      index = 0;
    }
  }

  private static class LimitedQueue<E> extends LinkedList<E> {

    private final int limit;

    public LimitedQueue(int limit) {
      this.limit = limit;
    }

    @Override
    public boolean add(E o) {
      super.add(o);
      while (size() > limit) {
        super.remove();
      }
      return true;
    }
  }

}