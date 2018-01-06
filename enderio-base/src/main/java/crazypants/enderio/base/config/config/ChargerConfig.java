package crazypants.enderio.base.config.config;

import crazypants.enderio.base.config.Config.Section;
import crazypants.enderio.base.config.SectionedValueFactory;
import crazypants.enderio.base.config.ValueFactory.IValue;

public final class ChargerConfig {

  public static final SectionedValueFactory F = new SectionedValueFactory(BaseConfig.F, new Section("", "wireless"));

  public static final IValue<Integer> wirelessChargerRange = F.make("wirelessChargerRange", 24, //
      "The range of the wireless charger").setRange(1, 160).sync();

}
