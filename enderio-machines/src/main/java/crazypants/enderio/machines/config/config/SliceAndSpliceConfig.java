package crazypants.enderio.machines.config.config;

import crazypants.enderio.base.config.factory.IValue;
import crazypants.enderio.base.config.factory.IValueFactory;
import crazypants.enderio.machines.config.Config;

public final class SliceAndSpliceConfig {

  public static final IValueFactory F = Config.F.section("slicensplice");

  public static final IValue<Float> toolDamageChance = F.make("toolDamageChance", 0.01f, //
      "The chance that a tool will take damage each tick while the Slice'n'Splice is running (0 = no chance, 1 = 100% chance). "
          + "Tools will always take damage when the crafting is finished.")
      .setRange(0, 1).sync();

}
