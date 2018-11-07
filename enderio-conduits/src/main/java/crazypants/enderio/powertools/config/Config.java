package crazypants.enderio.powertools.config;

import crazypants.enderio.base.config.factory.ValueFactory;
import crazypants.enderio.powertools.EnderIOPowerTools;

public final class Config {

  public static final ValueFactory F = new ValueFactory(EnderIOPowerTools.MODID);

  //

  public static void load() {
    // force sub-configs to be classloaded with the main config
    CapBankConfig.F.getClass();
    GaugeConfig.F.getClass();
    PersonalConfig.F.getClass();
  }
}
