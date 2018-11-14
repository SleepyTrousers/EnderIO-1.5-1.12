package crazypants.enderio.powertools.config;

import crazypants.enderio.base.config.factory.ValueFactoryEIO;
import crazypants.enderio.powertools.EnderIOPowerTools;

public final class Config {

  public static final ValueFactoryEIO F = new ValueFactoryEIO(EnderIOPowerTools.MODID);

  static {
    // force sub-configs to be classloaded with the main config
    CapBankConfig.F.getClass();
    GaugeConfig.F.getClass();
    PersonalConfig.F.getClass();
  }
}
