package crazypants.enderio.base.config.config;

import crazypants.enderio.base.config.factory.IValue;
import crazypants.enderio.base.config.factory.IValueFactory;

public final class ChargerConfig {

  public static final IValueFactory F = BaseConfig.F.section("wireless");

  public static final IValue<Integer> wirelessRange = F.make("wirelessRange", 24, //
      "The range of wireless chargers.").setRange(1, 160).sync();

}
