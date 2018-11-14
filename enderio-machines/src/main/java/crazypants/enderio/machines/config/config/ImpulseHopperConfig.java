package crazypants.enderio.machines.config.config;

import info.loenwind.autoconfig.factory.IValue;
import info.loenwind.autoconfig.factory.IValueFactory;
import crazypants.enderio.machines.config.Config;

public final class ImpulseHopperConfig {

  public static final IValueFactory F = Config.F.section("impulse_hopper");

  public static final IValue<Integer> impulseHopperWorkEveryTick = F
      .make("impulseHopperWorkEveryTick", 20, "How many ticks should it take for each operation? (Note: This scales quadratically with the capacitor)")
      .setRange(1, 20).sync();

}
