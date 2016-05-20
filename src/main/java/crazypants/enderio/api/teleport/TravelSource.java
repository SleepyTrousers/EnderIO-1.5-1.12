package crazypants.enderio.api.teleport;

import crazypants.enderio.EnderIO;
import crazypants.enderio.config.Config;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;

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

  public final SoundEvent sound;

  private TravelSource() {
    this("mob.endermen.portal");
  }

  private TravelSource(String sound) {
    this(new ResourceLocation(EnderIO.DOMAIN, sound));
  }

  private TravelSource(ResourceLocation sound) {
    this.sound = SoundEvent.REGISTRY.getObject(sound);
  }

  private TravelSource(SoundEvent sound) {
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