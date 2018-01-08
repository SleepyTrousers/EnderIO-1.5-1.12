package crazypants.enderio.machines.config.config;

import crazypants.enderio.base.config.Config.Section;
import crazypants.enderio.base.config.SectionedValueFactory;
import crazypants.enderio.base.config.ValueFactory.IValue;
import crazypants.enderio.machines.config.Config;

public final class VacuumConfig {

  public static final SectionedValueFactory F = new SectionedValueFactory(Config.F, new Section("", "vacuum"));

  public static final IValue<Integer> vacuumChestRange = F.make("vacuumChestRange", 6, //
      "The maximum range of the vacuum chest").setRange(1, 32).sync();

  public static final IValue<Integer> vacuumXPRange = F.make("vacuumXPRange", 6, //
      "The range of the XP vacuum").setRange(1, 32).sync();

  public static final IValue<Integer> vacuumChestMaxItems = F.make("vacuumChestMaxItems", 40, //
      "Maximum number of items the vacuum chest can effect at a time. (-1 for unlimited)").setMin(-1).sync();

}
