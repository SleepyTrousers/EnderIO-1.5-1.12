package crazypants.enderio.machines.config.config;

import crazypants.enderio.base.config.factory.IValue;
import crazypants.enderio.base.config.factory.IValueFactory;
import crazypants.enderio.machines.config.Config;

public final class CombustionGenConfig {

  public static final IValueFactory F = Config.F.section("generator.combustion");

  public static final IValue<Integer> combGenTankSize = F
      .make("combGenTankSize", 5000, "How large should the fuel and coolant tanks of the combustion generator be?").setRange(500, 50000).sync();

}
