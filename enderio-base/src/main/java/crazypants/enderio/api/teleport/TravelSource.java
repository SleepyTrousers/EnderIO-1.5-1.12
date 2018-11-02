package crazypants.enderio.api.teleport;

import crazypants.enderio.base.config.Config;
import crazypants.enderio.base.config.config.TeleportConfig;
import crazypants.enderio.base.sound.IModSound;
import crazypants.enderio.base.sound.SoundRegistry;

public enum TravelSource {

  BLOCK(SoundRegistry.TRAVEL_SOURCE_BLOCK) {
    @Override
    public int getMaxDistanceTravelled() {
      return TeleportConfig.rangeBlocks.get();
    }
  },
  STAFF(SoundRegistry.TRAVEL_SOURCE_ITEM) {
    @Override
    public int getMaxDistanceTravelled() {
      return Config.travelStaffMaximumDistance;
    }

    @Override
    public float getPowerCostPerBlockTraveledRF() {
      return Config.travelStaffPowerPerBlockRF;
    }
  },
  STAFF_BLINK(SoundRegistry.TRAVEL_SOURCE_ITEM) {
    @Override
    public int getMaxDistanceTravelled() {
      return Config.travelStaffMaxBlinkDistance;
    }

    @Override
    public float getPowerCostPerBlockTraveledRF() {
      return Config.travelStaffPowerPerBlockRF;
    }
  },
  TELEPAD(SoundRegistry.TELEPAD);

  public static int getMaxDistanceSq() {
    return STAFF.getMaxDistanceTravelledSq();
  }

  public final IModSound sound;

  private TravelSource(IModSound sound) {
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