package crazypants.enderio.machines.config.config;

import crazypants.enderio.base.fluid.Fluids;
import crazypants.enderio.machines.config.Config;
import crazypants.enderio.util.LimitedIntValue;
import info.loenwind.autoconfig.factory.IValue;
import info.loenwind.autoconfig.factory.IValueFactory;
import net.minecraftforge.fluids.Fluid;

public final class WeatherConfig {

  public static final IValueFactory F = Config.F.section("weather");

  public static final IValue<Integer> tankSize = F.make("tankSize", 8000, //
      "Size of the internal tank of the weather obelisk.").setMin(5).sync();

  public static final IValue<Fluid> weatherObeliskClearFluidType = F.makeFluid("weatherObeliskClearFluidType", Fluids.LIQUID_SUNSHINE.getName(), //
      "The fluid required to set the world to clear weather.").sync();
  public static final IValue<Integer> weatherObeliskClearFluidAmount = new LimitedIntValue(F.make("weatherObeliskClearFluidAmount", 2000, //
      "The fluid amount required (in mB) to set the world to clear weather.").setMin(5).sync(), null, tankSize);

  public static final IValue<Fluid> weatherObeliskRainFluidType = F.makeFluid("weatherObeliskRainFluidType", Fluids.CLOUD_SEED.getName(), //
      "The fluid required to set the world to rainy weather.").sync();
  public static final IValue<Integer> weatherObeliskRainFluidAmount = new LimitedIntValue(F.make("weatherObeliskRainFluidAmount", 500, //
      "The fluid amount required (in mB) to set the world to rainy weather.").setMin(5).sync(), null, tankSize);

  public static final IValue<Fluid> weatherObeliskThunderFluidType = F.makeFluid("weatherObeliskThunderFluidType", Fluids.CLOUD_SEED_CONCENTRATED.getName(), //
      "The fluid required to set the world to thundering weather.").sync();
  public static final IValue<Integer> weatherObeliskThunderFluidAmount = new LimitedIntValue(F.make("weatherObeliskThunderFluidAmount", 1000, //
      "The fluid amount required (in mB) to set the world to thundering weather.").setMin(5).sync(), null, tankSize);

}
