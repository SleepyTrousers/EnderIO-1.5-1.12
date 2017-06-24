package crazypants.enderio.integration.railcraft;

import crazypants.enderio.config.Config;
import crazypants.enderio.fluid.Fluids;
import net.minecraftforge.fml.common.event.FMLInterModComms;

public class RailcraftUtil {

  private RailcraftUtil() {
  }

  public static void registerFuels() {
    FMLInterModComms.sendMessage("railcraft", "boiler-fuel-liquid",
        Fluids.HOOTCH + "@" + (Config.hootchPowerPerCycleRF / 10 * Config.hootchPowerTotalBurnTime));
    FMLInterModComms.sendMessage("railcraft", "boiler-fuel-liquid",
        Fluids.ROCKET_FUEL + "@" + (Config.rocketFuelPowerPerCycleRF / 10 * Config.rocketFuelPowerTotalBurnTime));
    FMLInterModComms.sendMessage("railcraft", "boiler-fuel-liquid",
        Fluids.FIRE_WATER + "@" + (Config.fireWaterPowerPerCycleRF / 10 * Config.fireWaterPowerTotalBurnTime));
  }

}
