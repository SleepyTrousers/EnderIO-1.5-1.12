package crazypants.enderio.machines.machine.ihopper;

import crazypants.enderio.base.config.Config.Section;
import crazypants.enderio.base.config.SectionedValueFactory;
import crazypants.enderio.base.config.ValueFactory.IValue;
import crazypants.enderio.machines.config.Config;

public final class ImpulseHopperConfig {

  public static final SectionedValueFactory F = new SectionedValueFactory(Config.F, new Section("", "impulse_hopper"));

  public static final IValue<Integer> impulseHopperWorkEveryTick = F
      .make("impulseHopperWorkEveryTick", 20, "How many ticks should it take for each operation? (Note: This scales quadratically with the capacitor)")
      .setRange(1, 20).sync();

}
