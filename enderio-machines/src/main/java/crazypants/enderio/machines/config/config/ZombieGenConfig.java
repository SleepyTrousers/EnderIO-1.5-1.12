package crazypants.enderio.machines.config.config;

import crazypants.enderio.base.config.Config.Section;
import crazypants.enderio.base.config.SectionedValueFactory;
import crazypants.enderio.base.config.ValueFactory.IValue;
import crazypants.enderio.machines.config.Config;

public final class ZombieGenConfig {

  public static final SectionedValueFactory F = new SectionedValueFactory(Config.F, new Section("", "zombiegenerator"));

  public static final IValue<Integer> ticksPerBucketOfFuel = F.make("ticksPerBucketOfFuel", 10 * 60 * 20, //
  "The number of ticks one bucket of fuel lasts.").setMin(1).sync();
  public static final IValue<Float> minimumTankLevel = F.make("minimumTankLevel", 0.7f, //
  "How full does the tank need to be for the zombie head to produce energy?. (0.0-0.9995)").setRange(0, 0.9995).sync();

}
