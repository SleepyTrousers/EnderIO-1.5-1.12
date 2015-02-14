package crazypants.enderio.api.teleport;

import crazypants.enderio.config.Config;

public enum TravelSource {

  BLOCK(0, Config.travelAnchorMaxDistance),
  STAFF(Config.travelStaffPowerPerBlockRF, Config.travelStaffMaxDistance),
  STAFF_BLINK(Config.travelStaffPowerPerBlockRF, Config.travelStaffMaxBlinkDistance),
  TELEPAD(0, 0);

  public static int getMaxDistance() {
    return STAFF.maxDistanceTravelledSq;
  }

  public static int getMaxDistanceSq() {
    return STAFF.maxDistanceTravelledSq;
  }

  public final float powerCostPerBlockTraveledRF;
  public final int maxDistanceTravelled;
  public final int maxDistanceTravelledSq;

  private TravelSource(float powerCostPerBlockTraveled, int maxDistanceTravelled) {
    this.powerCostPerBlockTraveledRF = powerCostPerBlockTraveled;
    this.maxDistanceTravelled = maxDistanceTravelled;
    maxDistanceTravelledSq = maxDistanceTravelled * maxDistanceTravelled;
  }

  public boolean getConserveMomentum() {
    return this == STAFF_BLINK;
  }

}