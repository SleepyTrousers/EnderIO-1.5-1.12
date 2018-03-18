package crazypants.enderio.machines.config.config;

import crazypants.enderio.base.config.factory.IValue;
import crazypants.enderio.base.config.factory.IValueFactory;
import crazypants.enderio.machines.config.Config;

public final class WeatherConfig {

  public static final IValueFactory F = Config.F.section("weather");

  public static final IValue<Integer> weatherObeliskClearFluid = F.make("weatherObeliskClearFluid", 2000, //
      "The fluid required (in mB) to set the world to clear weather.").setMin(1).sync();
  public static final IValue<Integer> weatherObeliskRainFluid = F.make("weatherObeliskRainFluid", 500, //
      "The fluid required (in mB) to set the world to rainy weather.").setMin(1).sync();
  public static final IValue<Integer> weatherObeliskThunderFluid = F.make("weatherObeliskThunderFluid", 1000, //
      "The fluid required (in mB) to set the world to thundering weather.").setMin(1).sync();

}
