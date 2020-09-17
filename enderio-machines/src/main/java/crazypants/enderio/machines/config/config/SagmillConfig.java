package crazypants.enderio.machines.config.config;

import crazypants.enderio.machines.config.Config;
import info.loenwind.autoconfig.factory.IValue;
import info.loenwind.autoconfig.factory.IValueFactory;

public final class SagmillConfig {

  public static final IValueFactory F = Config.F.section("sagmill");

  public static final IValue<Boolean> respectsGravity = F.make("respectsGravitySimple", true,
      "If true, the Simple SAG Mill will respect gravity and fall like an anvil when not attached to a block.").sync();

}
