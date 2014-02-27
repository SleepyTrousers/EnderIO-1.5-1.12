package crazypants.enderio.conduit.power;

import java.util.LinkedList;

public class PowerTracker {

  private float previousStorageLevel = -1;

  private final TickTracker recTracker = new TickTracker();

  private final TickTracker sentTracker = new TickTracker();

  private float sentThisTick = 0;

  private float recievedThisTick = 0;

  public void tickStart(float storedEnergy) {
    float curStorage = storedEnergy;

    if(previousStorageLevel > -1) {
      float recieved = curStorage - previousStorageLevel;
      recieved = Math.max(0, recieved);
      recievedThisTick += recieved;
    }
  }

  public void powerRecieved(float power) {
    recievedThisTick += power;
  }

  public void powerSent(float power) {
    sentThisTick += power;
  }

  public void tickEnd(float storedEnergy) {
    previousStorageLevel = storedEnergy;
    sentTracker.tick(sentThisTick);
    recTracker.tick(recievedThisTick);
    recievedThisTick = 0;
    sentThisTick = 0;
  }

  public float getAverageMjTickRecieved() {
    return recTracker.getMJT();
  }

  public float getAverageMjTickSent() {
    return sentTracker.getMJT();
  }

  private static class TickTracker {

    private float lastSecondTotal = 0;
    private int index = 0;
    private LimitedQueue<Float> lastFiveSeconds = new LimitedQueue<Float>(5);

    float getMJT() {
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

    void tick(float power) {
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
