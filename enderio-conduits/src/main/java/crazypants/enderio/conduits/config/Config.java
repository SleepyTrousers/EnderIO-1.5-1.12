package crazypants.enderio.conduits.config;

import crazypants.enderio.base.config.factory.ValueFactoryEIO;
import crazypants.enderio.conduits.EnderIOConduits;

public final class Config {

  public static final ValueFactoryEIO F = new ValueFactoryEIO(EnderIOConduits.MODID);

  static {
    // force sub-configs to be classloaded with the main config
    ConduitConfig.F.getClass();
  }
}
