package crazypants.enderio.endergy.config;

import crazypants.enderio.base.config.factory.ValueFactoryEIO;
import crazypants.enderio.endergy.EnderIOEndergy;

public final class Config {

  public static final ValueFactoryEIO F = new ValueFactoryEIO(EnderIOEndergy.MODID);

  //

  public static void load() {
    EndergyConfig.F.getClass();
  }
}
