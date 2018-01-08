package crazypants.enderio.base.config.config;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.config.ValueFactory;

public final class BaseConfig {

  public static final ValueFactory F = new ValueFactory(EnderIO.MODID);

  public static void load() {
    ChargerConfig.F.getClass();
    EnchanterConfig.F.getClass();
    InfinityConfig.F.getClass();
    SpawnerConfig.F.getClass();
    UpgradeConfig.F.getClass();
  }

}
