package crazypants.enderio.machines.machine.ihopper;

import crazypants.enderio.base.config.factory.IValue;
import crazypants.enderio.base.config.factory.ValueFactory;
import crazypants.enderio.machines.config.Config;

public final class ImpulseHopperConfig {

  public static final ValueFactory F = Config.F.section("impulse_hopper");

  public static final IValue<Integer> impulseHopperWorkEveryTick = F
      .make("impulseHopperWorkEveryTick", 20, "How many ticks should it take for each operation? (Note: This scales quadratically with the capacitor)")
      .setRange(1, 20).sync();

}
