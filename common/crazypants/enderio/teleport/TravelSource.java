package crazypants.enderio.teleport;

import crazypants.enderio.Config;

public enum TravelSource {

  BLOCK(0, Config.travelAnchorMaxDistance),
  STAFF(Config.travelStaffPowerPerBlock * 10, Config.travelStaffMaxDistance);

  public final float powerCostPerBlockTraveledRF;
  public final int maxDistanceTravelled;
  public final int maxDistanceTravelledSq;

  private TravelSource(float powerCostPerBlockTraveled, int maxDistanceTravelled) {
    this.powerCostPerBlockTraveledRF = powerCostPerBlockTraveled;
    this.maxDistanceTravelled = maxDistanceTravelled;
    maxDistanceTravelledSq = maxDistanceTravelled * maxDistanceTravelled;
  }

  public static int getMaxDistance() {
    //    int max = 0;
    //    for (TravelSource source : TravelSource.values()) {
    //      if(source.maxDistanceTravelled > max) {
    //        max = source.maxDistanceTravelled;
    //      }
    //    }
    //    return max;
    return STAFF.maxDistanceTravelledSq;
  }

  public static int getMaxDistanceSq() {
    //    int max = 0;
    //    for (TravelSource source : TravelSource.values()) {
    //      if(source.maxDistanceTravelledSq > max) {
    //        max = source.maxDistanceTravelledSq;
    //      }
    //    }
    //    return max;
    return STAFF.maxDistanceTravelledSq;
  }

}