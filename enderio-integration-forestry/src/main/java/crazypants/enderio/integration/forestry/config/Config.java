package crazypants.enderio.integration.forestry.config;

import crazypants.enderio.base.config.factory.ValueFactory;
import crazypants.enderio.integration.forestry.EnderIOIntegrationForestry;

public final class Config {

  public static final ValueFactory F = new ValueFactory(EnderIOIntegrationForestry.MODID);

  //

  public static void load() {
    // force sub-configs to be classloaded with the main config
    ForestryConfig.F.getClass();
  }
}
