package crazypants.enderio.api.teleport;

import crazypants.enderio.EnderIO;
import crazypants.enderio.config.Config;

public enum TravelSource {

  BLOCK() {
    @Override
    public int getMaxDistanceTravelled() {
      return Config.travelAnchorMaxDistance;
    }
  },
  STAFF() {
    @Override
    public int getMaxDistanceTravelled() {
      return Config.travelStaffMaxDistance;
    }

    @Override
    public float getPowerCostPerBlockTraveledRF() {
      return Config.travelStaffPowerPerBlockRF;
    }
  },
  STAFF_BLINK() {
    @Override
    public int getMaxDistanceTravelled() {
      return Config.travelStaffMaxBlinkDistance;
    }

    @Override
    public float getPowerCostPerBlockTraveledRF() {
      return Config.travelStaffPowerPerBlockRF;
    }
  },
  TELEPAD(EnderIO.DOMAIN + ":telepad.teleport");

  public static int getMaxDistance() {
    return STAFF.getMaxDistanceTravelledSq();
  }

  public static int getMaxDistanceSq() {
    return STAFF.getMaxDistanceTravelledSq();
  }

  public final String sound;

  private TravelSource() {
    this("mob.endermen.portal");
  }

  private TravelSource(String sound) {
    this.sound = sound;
  }

  public boolean getConserveMomentum() {
    return this == STAFF_BLINK;
  }

  public int getMaxDistanceTravelled() {
    return 0;
  }

  public int getMaxDistanceTravelledSq() {
    return getMaxDistanceTravelled() * getMaxDistanceTravelled();
  }

  public float getPowerCostPerBlockTraveledRF() {
    return 0;
  }

}