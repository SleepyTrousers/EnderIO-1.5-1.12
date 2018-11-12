package crazypants.enderio.integration.forestry.config;

import crazypants.enderio.base.config.factory.ValueFactoryEIO;
import crazypants.enderio.integration.forestry.EnderIOIntegrationForestry;

public final class Config {

  public static final ValueFactoryEIO F = new ValueFactoryEIO(EnderIOIntegrationForestry.MODID);

  //

  public static void load() {
    // force sub-configs to be classloaded with the main config
    ForestryConfig.F.getClass();
  }
}
