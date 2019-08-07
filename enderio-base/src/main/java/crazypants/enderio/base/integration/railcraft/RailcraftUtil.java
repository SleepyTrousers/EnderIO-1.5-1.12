package crazypants.enderio.base.integration.railcraft;

import javax.annotation.Nonnull;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.events.EnderIOLifecycleEvent;
import crazypants.enderio.base.farming.FarmersRegistry;
import crazypants.enderio.base.fluid.FluidFuelRegister;
import crazypants.enderio.base.fluid.Fluids;
import crazypants.enderio.base.fluid.IFluidFuel;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber(modid = EnderIO.MODID)
public class RailcraftUtil {

  public static void registerFuels() {
    // TODO: Check if this is still valid
    IFluidFuel hootch = FluidFuelRegister.instance.getFuel(Fluids.HOOTCH.getFluid());
    if (hootch != null) {
      FMLInterModComms.sendMessage("railcraft", "boiler-fuel-liquid",
          Fluids.HOOTCH.getName() + "@" + (hootch.getPowerPerCycle() / 10 * hootch.getTotalBurningTime()));
    }
    IFluidFuel rocket_fuel = FluidFuelRegister.instance.getFuel(Fluids.ROCKET_FUEL.getFluid());
    if (rocket_fuel != null) {
      FMLInterModComms.sendMessage("railcraft", "boiler-fuel-liquid",
          Fluids.ROCKET_FUEL.getName() + "@" + (rocket_fuel.getPowerPerCycle() / 10 * rocket_fuel.getTotalBurningTime()));
    }
    IFluidFuel fire_water = FluidFuelRegister.instance.getFuel(Fluids.FIRE_WATER.getFluid());
    if (fire_water != null) {
      FMLInterModComms.sendMessage("railcraft", "boiler-fuel-liquid",
          Fluids.FIRE_WATER.getName() + "@" + (fire_water.getPowerPerCycle() / 10 * fire_water.getTotalBurningTime()));
    }
  }

  @SubscribeEvent
  public static void registerHoes(@Nonnull EnderIOLifecycleEvent.Init.Pre event) {
    FarmersRegistry.registerHoes("railcraft", "tool_hoe_steel");
  }

}
