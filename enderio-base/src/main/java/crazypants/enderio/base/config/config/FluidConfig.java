package crazypants.enderio.base.config.config;

import info.loenwind.autoconfig.factory.IValue;
import info.loenwind.autoconfig.factory.IValueFactory;

public final class FluidConfig {

  public static final IValueFactory F = BaseConfig.F.section("fluid");

  public static final IValue<Integer> hootchPowerPerCycle = F.make("hootchPowerPerCycle", 60, //
      "Hootch: The amount of power generated per cycle.").setRange(1, 1000).sync();
  public static final IValue<Integer> hootchPowerTotalBurnTime = F.make("hootchPowerTotalBurnTime", 6000, //
      "Hootch: The total burn time. Examples.").setRange(1, 1000000).sync();

  public static final IValue<Integer> rocketFuelPowerPerCycle = F.make("rocketFuelPowerPerCycle", 160, //
      "Rocket Fuel: The amount of power generated per cycle.").setRange(1, 1000).sync();
  public static final IValue<Integer> rocketFuelPowerTotalBurnTime = F.make("rocketFuelPowerTotalBurnTime", 7000, //
      "Rocket Fuel: The total burn time. Examples.").setRange(1, 1000000).sync();

  public static final IValue<Integer> fireWaterPowerPerCycle = F.make("fireWaterPowerPerCycle", 80, //
      "Fire Water: The amount of power generated per cycle.").setRange(1, 1000).sync();
  public static final IValue<Integer> fireWaterPowerTotalBurnTime = F.make("fireWaterPowerTotalBurnTime", 15000, //
      "Fire Water: The total burn time. Examples.").setRange(1, 1000000).sync();

  public static final IValue<Integer> nutrientFoodBoostDelay = F.make("nutrientFoodBoostDelay", 400, //
      "The delay in ticks between when nutrient distillation boosts your food value.").setMin(1).sync();

  public static final IValue<Boolean> rocketFuelIsExplosive = F.make("rocketFuelIsExplosive", true, //
      "If enabled, Rocket Fuel will explode when in contact with fire.").sync();

}
