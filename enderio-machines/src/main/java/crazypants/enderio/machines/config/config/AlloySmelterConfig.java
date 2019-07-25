package crazypants.enderio.machines.config.config;

import crazypants.enderio.machines.config.Config;
import crazypants.enderio.machines.machine.alloy.OperatingProfile;
import info.loenwind.autoconfig.factory.IValue;
import info.loenwind.autoconfig.factory.IValueFactory;

public final class AlloySmelterConfig {

  public static final IValueFactory F = Config.F.section("alloysmelter");

  public enum Profile {
    SIMPLE_ALLOY(OperatingProfile.SIMPLE_ALLOY),
    SIMPLE_FURNACE(OperatingProfile.SIMPLE_FURNACE),
    ALLOY_ONLY(OperatingProfile.ALLOY_ONLY),
    FURNACE_ONLY(OperatingProfile.FURNACE_ONLY),
    AUTO(OperatingProfile.AUTO);

    private final OperatingProfile profile;

    private Profile(OperatingProfile profile) {
      this.profile = profile;
    }

    public OperatingProfile get() {
      return profile;
    }
  }

  public static final IValue<Profile> profileSimpleAlloy = F.make("profileSimpleAlloy", Profile.SIMPLE_ALLOY, //
      "Operating profile for the Simple Alloy Smelter. Restaring the game is needed for a change to be reflected in JEI.").sync();
  public static final IValue<Profile> profileSimpleFurnace = F.make("profileSimpleFurnace", Profile.SIMPLE_FURNACE, //
      "Operating profile for the Simple Furnace. Restaring the game is needed for a change to be reflected in JEI.").sync();
  public static final IValue<Profile> profileNormal = F.make("profileNormal", Profile.AUTO, //
      "Operating profile for the Alloy Smelter. Restaring the game is needed for a change to be reflected in JEI.").sync();
  public static final IValue<Profile> profileEnhancedAlloy = F.make("profileEnhancedAlloy", Profile.AUTO, //
      "Operating profile for the Enhanced Alloy Smelter. Restaring the game is needed for a change to be reflected in JEI.").sync();

}
