package crazypants.enderio.base.config.config;

import crazypants.enderio.base.config.Config.Section;
import crazypants.enderio.base.config.SectionedValueFactory;
import crazypants.enderio.base.config.ValueFactory.IValue;

public final class ChargerConfig {

  public static final SectionedValueFactory F = new SectionedValueFactory(BaseConfig.F, new Section("", "wireless"));

  public static final IValue<Integer> wirelessRange = F.make("wirelessRange", 24, //
      "The range of wireless chargers.").setRange(1, 160).sync();

}
