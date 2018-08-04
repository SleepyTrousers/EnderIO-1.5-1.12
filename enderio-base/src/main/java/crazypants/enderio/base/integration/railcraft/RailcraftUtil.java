package crazypants.enderio.base.integration.railcraft;

import javax.annotation.Nonnull;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.config.Config;
import crazypants.enderio.base.events.EnderIOLifecycleEvent;
import crazypants.enderio.base.farming.FarmersRegistry;
import crazypants.enderio.base.fluid.Fluids;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber(modid = EnderIO.MODID)
public class RailcraftUtil {

  public static void registerFuels() {
    FMLInterModComms.sendMessage("railcraft", "boiler-fuel-liquid",
        Fluids.HOOTCH + "@" + (Config.hootchPowerPerCycleRF / 10 * Config.hootchPowerTotalBurnTime));
    FMLInterModComms.sendMessage("railcraft", "boiler-fuel-liquid",
        Fluids.ROCKET_FUEL + "@" + (Config.rocketFuelPowerPerCycleRF / 10 * Config.rocketFuelPowerTotalBurnTime));
    FMLInterModComms.sendMessage("railcraft", "boiler-fuel-liquid",
        Fluids.FIRE_WATER + "@" + (Config.fireWaterPowerPerCycleRF / 10 * Config.fireWaterPowerTotalBurnTime));
  }

  @SubscribeEvent
  public static void registerHoes(@Nonnull EnderIOLifecycleEvent.Init.Pre event) {
    FarmersRegistry.registerHoes("railcraft", "tool_hoe_steel");
  }

}
