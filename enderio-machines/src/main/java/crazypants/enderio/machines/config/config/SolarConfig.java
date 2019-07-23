package crazypants.enderio.machines.config.config;

import com.enderio.core.common.util.NNList;

import crazypants.enderio.machines.config.Config;
import info.loenwind.autoconfig.factory.IValue;
import info.loenwind.autoconfig.factory.IValueFactory;

public final class SolarConfig {

  public static final IValueFactory F = Config.F.section("generator.solar");

  public static final NNList<IValue<Integer>> upgradeGen = new NNList<>(//
      F.make("darkSteelSolar0Gen", 10, //
          "Energy generated per SECOND by the Simple Solar upgrade. Split between all equipped DS armors.").setMin(1).sync(),
      F.make("darkSteelSolar1Gen", 40, //
          "Energy generated per SECOND by the Solar I upgrade. Split between all equipped DS armors.").setMin(1).sync(),
      F.make("darkSteelSolar2Gen", 80, //
          "Energy generated per SECOND by the Solar II upgrade. Split between all equipped DS armors.").setMin(1).sync(),
      F.make("darkSteelSolar3Gen", 160, //
          "Energy generated per SECOND by the Solar III upgrade. Split between all equipped DS armors.").setMin(1).sync());

  public static final NNList<IValue<Integer>> upgradeCost = new NNList<>(//
      F.make("darkSteelSolar0Cost", 4, //
          "Cost in XP levels of the Simple Solar upgrade.").setMin(1).sync(),
      F.make("darkSteelSolar1Cost", 8, //
          "Cost in XP levels of the Solar I upgrade.").setMin(1).sync(),
      F.make("darkSteelSolar2Cost", 16, //
          "Cost in XP levels of the Solar II upgrade.").setMin(1).sync(),
      F.make("darkSteelSolar3Cost", 24, //
          "Cost in XP levels of the Solar III upgrade.").setMin(1).sync());

  public static final IValue<Boolean> helmetChargeOthers = F.make("helmetChargeOthers", true, //
      "If enabled allows the solar upgrade to charge non-darksteel armors that the player is wearing.").sync();

  public static final NNList<IValue<Integer>> blockGen = new NNList<>(//
      F.make("solarPanel0Gen", 10, //
          "Energy generated per TICK by Simple Photovoltaic Panels.").setMin(1).sync(),
      F.make("solarPanel1Gen", 40, //
          "Energy generated per TICK by Photovoltaic Panels.").setMin(1).sync(),
      F.make("solarPanel2Gen", 80, //
          "Energy generated per TICK by Advanced Photovoltaic Panels.").setMin(1).sync(),
      F.make("solarPanel3Gen", 160, //
          "Energy generated per TICK by Vibrant Photovoltaic Panels.").setMin(1).sync());

  public static final IValue<Boolean> canSolarTypesJoin = F.make("canSolarTypesJoin", false, //
      "When enabled Photovoltaic Panels of different kinds can join together as a multi-block").sync();
  public static final IValue<Integer> solarRecalcSunTick = F.make("solarRecalcSunTick", 5 * 20, //
      "How often (in ticks) the Photovoltaic Panels should check the sun's angle.").setMin(1).sync();

  public static final IValue<Boolean> solarPoweredBySunshine = F.make("solarPoweredBySunshine", true, //
      "When enabled Photovoltaic Panels accept Liquid Sunshine directly above them as source of sunlight.").sync();

}
