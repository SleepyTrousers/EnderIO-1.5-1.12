package crazypants.enderio.conduit.power;

import crazypants.enderio.power.PerTickIntAverageCalculator;

public class PowerTracker {

    private long previousStorageLevel = -1;

    private final PerTickIntAverageCalculator recTracker = new PerTickIntAverageCalculator();

    private final PerTickIntAverageCalculator sentTracker = new PerTickIntAverageCalculator();

    private int sentThisTick = 0;

    private int recievedThisTick = 0;

    public void tickStart(long storedEnergy) {
        long curStorage = storedEnergy;

        if (previousStorageLevel > -1) {
            long recieved = curStorage - previousStorageLevel;
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

    public void tickEnd(long storedEnergy) {
        previousStorageLevel = storedEnergy;
        sentTracker.tick(sentThisTick);
        recTracker.tick(recievedThisTick);
        recievedThisTick = 0;
        sentThisTick = 0;
    }

    public float getAverageRfTickRecieved() {
        return recTracker.getAverage();
    }

    public float getAverageRfTickSent() {
        return sentTracker.getAverage();
    }
}
