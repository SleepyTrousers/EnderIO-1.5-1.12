package crazypants.enderio.machines.config.config;

import crazypants.enderio.base.config.factory.IValue;
import crazypants.enderio.base.config.factory.IValueFactory;
import crazypants.enderio.machines.config.Config;

public final class LavaGenConfig {

  public static final IValueFactory F = Config.F.section("generator.lavagen");

  public static final IValue<Integer> heatGain = F.make("heatGain", 4, //
      "The amount of heat gained per tick when generating.").setRange(0, 64).sync();

  public static final IValue<Integer> heatLossPassive = F.make("heatLossPassive", 1, //
      "The amount of heat lost per tick.").setRange(0, 64).sync();

  public static final IValue<Integer> heatLossActive = F.make("heatLossActive", 1, //
      "The amount of heat lost per tick when actively cooled with a cold fluid block. Note that only one neighbor is checked per tick.").setRange(0, 64)
      .sync();

  public static final IValue<Integer> maxHeatFactor = F.make("maxHeatFactor", 8, //
      "The maximum heat that can be reached, as factor of the time it takes to process one bucket of lava.").setRange(1, 64).sync();

  public static final IValue<Boolean> useVanillaBurnTime = F.make("useVanillaBurnTime", true, //
      "When enabled, the vanilla burn time for a bucket of lava is used. When disabled, the current burn time "
          + "(which could be changed by any mod) is used.")
      .sync();

  public static final IValue<Float> activeCoolingEvaporatesWater = F.make("activeCoolingEvaporatesWater", .5f, //
      "Chance that active cooling will evaporate water blocks. Set to 0 to disable.").setRange(0, 1).sync();

  public static final IValue<Float> activeCoolingLiquefiesIce = F.make("activeCoolingLiquefiesIce", .25f, //
      "Chance that active cooling will liquefy icd blocks. Set to 0 to disable.").setRange(0, 1).sync();

  public static final IValue<Float> minEfficiency = F.make("minEfficiency", .05f, //
      "The minimum efficiency the machine will not go under even when fully heated up.").setRange(0, 1).sync();

  public static final IValue<Integer> tankSize = F.make("tankSize", 4000, //
      "The size of the lava tank.").setRange(1, 64000).sync();

  public static final IValue<Float> overheatThreshold = F.make("overheatThreshold", .8f, //
      "The heat percentage above which the machine will overheat and set things on fire.").setRange(0, 1).sync();

}
