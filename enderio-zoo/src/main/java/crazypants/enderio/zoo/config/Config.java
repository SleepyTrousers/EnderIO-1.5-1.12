package crazypants.enderio.zoo.config;

import crazypants.enderio.base.config.factory.ValueFactoryEIO;
import crazypants.enderio.zoo.EnderIOZoo;

public final class Config {

  public static final ValueFactoryEIO F = new ValueFactoryEIO(EnderIOZoo.MODID);

  //

  public static void load() {
    // force sub-configs to be classloaded with the main config
    ZooConfig.F.getClass();
  }
}
