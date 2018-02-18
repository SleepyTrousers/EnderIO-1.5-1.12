package crazypants.enderio.base.config.config;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.config.ValueFactory;

public final class BaseConfig {

  public static final ValueFactory F = new ValueFactory(EnderIO.MODID);

  public static void load() {
    ChargerConfig.F.getClass();
    DiagnosticsConfig.F.getClass();
    PersonalConfig.F.getClass();
    DarkSteelConfig.F.getClass();
    EnchanterConfig.F.getClass();
    InfinityConfig.F.getClass();
    RecipeConfig.F.getClass();
    BrokenSpawnerConfig.F.getClass();
    TopConfig.F.getClass();
  }

}
