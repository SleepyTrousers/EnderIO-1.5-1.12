package crazypants.enderio.conduit.power;

import java.util.LinkedList;

public class PowerTracker {

  private float previousStorageLevel = -1;

  private final TickTracker recTracker = new TickTracker();

  private final TickTracker sentTracker = new TickTracker();

  private int sentThisTick = 0;

  private int recievedThisTick = 0;

  public void tickStart(float storedEnergy) {
    double curStorage = storedEnergy;

    if(previousStorageLevel > -1) {
      double recieved = curStorage - previousStorageLevel;
      recieved = Math.max(0, recieved);
      recievedThisTick += recieved;
    }
  }

  public void powerRecieved(int power) {
    recievedThisTick += power;
  }

  public void powerSent(int power) {
    sentThisTick += power;
  }

  public void tickEnd(int storedEnergy) {
    previousStorageLevel = storedEnergy;
    sentTracker.tick(sentThisTick);
    recTracker.tick(recievedThisTick);
    recievedThisTick = 0;
    sentThisTick = 0;
  }

  public float getAverageRfTickRecieved() {
    return recTracker.getRFT();
  }

  public float getAverageRfTickSent() {
    return sentTracker.getRFT();
  }

  private static class TickTracker {

    private float lastSecondTotal = 0;
    private int index = 0;
    private LimitedQueue<Float> lastFiveSeconds = new LimitedQueue<Float>(5);

    float getRFT() {
      int numTicks = index + (lastFiveSeconds.size() * 20);
      if(numTicks == 0) {
        return 0;
      }
      float totalPower = lastSecondTotal;
      for (Float fl : lastFiveSeconds) {
        totalPower += fl;
      }
      return totalPower / numTicks;
    }

    void tick(int power) {
      lastSecondTotal += power;
      index++;
      if(index == 20) {
        lastFiveSeconds.add(lastSecondTotal);
        lastSecondTotal = 0;
        index = 0;
      }
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
