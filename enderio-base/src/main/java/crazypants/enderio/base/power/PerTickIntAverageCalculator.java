package crazypants.enderio.base.power;

import javax.annotation.Nonnull;

public class PerTickIntAverageCalculator {

  private float lastSecondTotal = 0;
  private int tickCount = 0;
  private final @Nonnull float[] secondsCache;
  private int writeIndex;
  private int writeSize;

  public PerTickIntAverageCalculator() {
    this(5);
  }

  public PerTickIntAverageCalculator(int numSeconds) {
    secondsCache = new float[numSeconds];
  }

  public float getAverage() {
    int numTicks = tickCount + writeSize * 20;
    if (numTicks == 0) {
      return 0;
    }
    float totalPower = lastSecondTotal;
    for (int idx = writeIndex, cnt = writeSize; cnt-- > 0;) {
      totalPower += secondsCache[idx];
      if (++idx == secondsCache.length) {
        idx = 0;
      }
    }
    return totalPower / numTicks;
  }

  public void tick(long value) {
    lastSecondTotal += value;
    tickCount++;
    if (tickCount == 20) {
      secondsCache[writeIndex++] = lastSecondTotal;
      if (writeIndex > writeSize) {
        writeSize = writeIndex;
      }
      if (writeIndex == secondsCache.length) {
        writeIndex = 0;
      }
      lastSecondTotal = 0;
      tickCount = 0;
    }
  }
}