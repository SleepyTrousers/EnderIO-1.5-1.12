package crazypants.enderio.machines.config.config;

import crazypants.enderio.base.config.Config.Section;
import crazypants.enderio.base.config.SectionedValueFactory;
import crazypants.enderio.base.config.ValueFactory.IValue;
import crazypants.enderio.machines.config.Config;

public final class CombustionGenConfig {

  public static final SectionedValueFactory F = new SectionedValueFactory(Config.F, new Section("", "generator.combustion"));

  public static final IValue<Float> enahancedCombGenQuality = F
      .make("enhancedCombGenQuality", 1.5f,
          "How much better than the normal combustion generator is the enhanced one? This effects the tank size and the energy generated per tick.")
      .setRange(1, 10).sync();

  public static final IValue<Integer> combGenTankSize = F
      .make("combGenTankSize", 5000, "How large should the fuel and coolant tanks of the combustion generator be?").setRange(500, 50000).sync();

}
