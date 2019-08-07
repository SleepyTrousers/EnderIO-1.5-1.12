package crazypants.enderio.base.config.config;

import info.loenwind.autoconfig.factory.IValue;
import info.loenwind.autoconfig.factory.IValueFactory;

public final class FluidConfig {

  public static final IValueFactory F = BaseConfig.F.section("fluid");

  public static final IValue<Integer> nutrientFoodBoostDelay = F.make("nutrientFoodBoostDelay", 400, //
      "The delay in ticks between when nutrient distillation boosts your food value.").setMin(1).sync();

  public static final IValue<Boolean> rocketFuelIsExplosive = F.make("rocketFuelIsExplosive", true, //
      "If enabled, Rocket Fuel will explode when in contact with fire.").sync();

}
