package crazypants.enderio.api.teleport;

import crazypants.enderio.EnderIO;
import crazypants.enderio.config.Config;

public enum TravelSource {

  BLOCK(0, Config.travelAnchorMaxDistance),
  STAFF(Config.travelStaffPowerPerBlockRF, Config.travelStaffMaxDistance),
  STAFF_BLINK(Config.travelStaffPowerPerBlockRF, Config.travelStaffMaxBlinkDistance),
  TELEPAD(0, 0, EnderIO.MODID + ":telepad.teleport");

  public static int getMaxDistance() {
    return STAFF.maxDistanceTravelledSq;
  }

  public static int getMaxDistanceSq() {
    return STAFF.maxDistanceTravelledSq;
  }

  public final float powerCostPerBlockTraveledRF;
  public final int maxDistanceTravelled;
  public final int maxDistanceTravelledSq;
  public final String sound;

  private TravelSource(float powerCostPerBlockTraveled, int maxDistanceTravelled) {
    this(powerCostPerBlockTraveled, maxDistanceTravelled, "mob.endermen.portal");
  }

  private TravelSource(float powerCostPerBlockTraveled, int maxDistanceTravelled, String sound) {
    this.powerCostPerBlockTraveledRF = powerCostPerBlockTraveled;
    this.maxDistanceTravelled = maxDistanceTravelled;
    maxDistanceTravelledSq = maxDistanceTravelled * maxDistanceTravelled;
    this.sound = sound;
  }

  public boolean getConserveMomentum() {
    return this == STAFF_BLINK;
  }

}