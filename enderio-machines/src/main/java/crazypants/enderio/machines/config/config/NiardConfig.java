package crazypants.enderio.machines.config.config;

import crazypants.enderio.machines.config.Config;
import info.loenwind.autoconfig.factory.IValue;
import info.loenwind.autoconfig.factory.IValueFactory;

public final class NiardConfig {

  public static final IValueFactory F = Config.F.section("niard");

  public static final IValue<Boolean> allowWaterInHell = F.make("allowWaterInHell", false, //
      "Is the Niard allowed to place water in the Nether?").sync();

}
