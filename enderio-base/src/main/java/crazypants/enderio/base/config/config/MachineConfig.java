package crazypants.enderio.base.config.config;

import crazypants.enderio.base.config.factory.IValueFactoryEIO;
import info.loenwind.autoconfig.factory.IValue;

public final class MachineConfig {

  public static final IValueFactoryEIO F = BaseConfig.F.section("machines");

  public static final IValue<Integer> sleepBetweenFailedTries = F.make("sleepBetweenFailedTries", 20, //
      "When a machine doesn't find a recipe for its inputs, how long (in ticks) should it wait until retrying? "
          + "Increasing this can increase tps on bigger servers but will create awkward pauses until machines (re-)start after being idle or out of power.")
      .setRange(5, 20 * 30).sync();

}
