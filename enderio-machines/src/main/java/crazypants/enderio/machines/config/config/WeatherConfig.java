package crazypants.enderio.machines.config.config;

import crazypants.enderio.base.config.Config.Section;
import crazypants.enderio.base.config.SectionedValueFactory;
import crazypants.enderio.base.config.ValueFactory.IValue;
import crazypants.enderio.machines.config.Config;

public final class WeatherConfig {

  public static final SectionedValueFactory F = new SectionedValueFactory(Config.F, new Section("", "weather"));

  public static final IValue<Integer> weatherObeliskClearFluid = F.make("weatherObeliskClearFluid", 2000, //
      "The fluid required (in mB) to set the world to clear weather.").setMin(1).sync();
  public static final IValue<Integer> weatherObeliskRainFluid = F.make("weatherObeliskRainFluid", 500, //
      "The fluid required (in mB) to set the world to rainy weather.").setMin(1).sync();
  public static final IValue<Integer> weatherObeliskThunderFluid = F.make("weatherObeliskThunderFluid", 1000, //
      "The fluid required (in mB) to set the world to thundering weather.").setMin(1).sync();

}
