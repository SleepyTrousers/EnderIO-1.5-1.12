package crazypants.enderio.powertools.config;

import crazypants.enderio.base.config.factory.IValue;
import crazypants.enderio.base.config.factory.IValueFactory;

public final class GaugeConfig {

  public static final IValueFactory F = Config.F.section("gauge");

  public static final IValue<Integer> updates = F.make("updateFrequency", 5, //
      "How often (in ticks) the Gauge should be updated. This involves a server roundtrip! (client setting)").setRange(1, 200);

  public static final IValue<Integer> updateLimit = F.make("updateFrequencyLimit", 5, //
      "How often (in ticks) the Gauge should query its neighbors for data. (server setting)").setRange(1, 200).sync();

}
