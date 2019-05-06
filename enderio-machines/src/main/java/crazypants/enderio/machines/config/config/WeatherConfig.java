package crazypants.enderio.machines.config.config;

import crazypants.enderio.base.fluid.Fluids;
import crazypants.enderio.machines.config.Config;
import info.loenwind.autoconfig.factory.IValue;
import info.loenwind.autoconfig.factory.IValueFactory;
import net.minecraftforge.fluids.Fluid;

public final class WeatherConfig {

  public static final IValueFactory F = Config.F.section("weather");

  public static final IValue<Fluid> weatherObeliskClearFluidType = F.makeFluid("weatherObeliskClearFluidType", Fluids.LIQUID_SUNSHINE.getName(), //
      "The fluid required to set the world to clear weather.").sync();
  public static final IValue<Integer> weatherObeliskClearFluidAmount = F.make("weatherObeliskClearFluidAmount", 2000, //
      "The fluid amount required (in mB) to set the world to clear weather.").setMin(1).sync();

  public static final IValue<Fluid> weatherObeliskRainFluidType = F.makeFluid("weatherObeliskRainFluidType", Fluids.CLOUD_SEED.getName(), //
      "The fluid required to set the world to rainy weather.").sync();
  public static final IValue<Integer> weatherObeliskRainFluidAmount = F.make("weatherObeliskRainFluidAmount", 500, //
      "The fluid amount required (in mB) to set the world to rainy weather.").setMin(1).sync();

  public static final IValue<Fluid> weatherObeliskThunderFluidType = F.makeFluid("weatherObeliskThunderFluidType", Fluids.CLOUD_SEED_CONCENTRATED.getName(), //
      "The fluid required to set the world to thundering weather.").sync();
  public static final IValue<Integer> weatherObeliskThunderFluidAmount = F.make("weatherObeliskThunderFluidAmount", 1000, //
      "The fluid amount required (in mB) to set the world to thundering weather.").setMin(1).sync();

}
