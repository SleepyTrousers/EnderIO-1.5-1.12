package crazypants.enderio.base.config.config;

import crazypants.enderio.base.config.ValueFactory;
import crazypants.enderio.base.network.PacketHandler;

public final class BaseConfig {

  public static final ValueFactory F = new ValueFactory(PacketHandler.INSTANCE);

  static {
    ChargerConfig.F.getClass();
    SpawnerConfig.F.getClass();
  }

}
