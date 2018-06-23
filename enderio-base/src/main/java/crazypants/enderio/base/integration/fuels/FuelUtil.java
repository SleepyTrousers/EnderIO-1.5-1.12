package crazypants.enderio.base.integration.fuels;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.Log;
import crazypants.enderio.base.fluid.FluidFuelRegister;
import net.minecraftforge.fluids.FluidRegistry.FluidRegisterEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber(modid = EnderIO.MODID)
public class FuelUtil {

  @SubscribeEvent
  public static void onFluidRegisterEvent(FluidRegisterEvent event) {
    switch (event.getFluidName()) {
    case "creosote":
      FluidFuelRegister.instance.addFuel(event.getFluidName(), 20, 5000);
      break;
    case "coal":
      FluidFuelRegister.instance.addFuel(event.getFluidName(), 40, 10000);
      break;
    case "crude_oil":
      FluidFuelRegister.instance.addFuel(event.getFluidName(), 50, 8000);
      break;
    case "tree_oil":
      FluidFuelRegister.instance.addFuel(event.getFluidName(), 50, 20000);
      break;
    case "refined_oil":
      FluidFuelRegister.instance.addFuel(event.getFluidName(), 100, 12500);
      break;
    case "refined_fuel":
      FluidFuelRegister.instance.addFuel(event.getFluidName(), 200, 10000);
      break;
    case "seed_oil":
      FluidFuelRegister.instance.addFuel(event.getFluidName(), 20, 4000);
      break;
    case "refined_biofuel":
      FluidFuelRegister.instance.addFuel(event.getFluidName(), 125, 6400);
      break;
    case "canolaoil":
      FluidFuelRegister.instance.addFuel(event.getFluidName(), 20, 4000);
      break;
    case "refinedcanolaoil":
      FluidFuelRegister.instance.addFuel(event.getFluidName(), 40, 5000);
      break;
    case "crystaloil":
      FluidFuelRegister.instance.addFuel(event.getFluidName(), 80, 5000);
      break;
    case "empoweredoil":
      FluidFuelRegister.instance.addFuel(event.getFluidName(), 140, 5000);
      break;
    case "bio.ethanol ":
      FluidFuelRegister.instance.addFuel(event.getFluidName(), 160, 3125);
      break;
    case "biodiesel":
      FluidFuelRegister.instance.addFuel(event.getFluidName(), 125, 4000);
      break;
    case "oil":
      FluidFuelRegister.instance.addFuel(event.getFluidName(), 50, 8000);
      break;
    case "diesel":
      FluidFuelRegister.instance.addFuel(event.getFluidName(), 125, 6400);
      break;
    case "gasoline":
      FluidFuelRegister.instance.addFuel(event.getFluidName(), 160, 7500);
      break;
    case "ic2biogas":
      FluidFuelRegister.instance.addFuel(event.getFluidName(), 50, 2000);
      break;
    case "biofuel":
      FluidFuelRegister.instance.addFuel(event.getFluidName(), 125, 4000);
      break;
    case "cryotheum":
      FluidFuelRegister.instance.addCoolant(event.getFluidName(), 0.0276f);
      break;
    case "if.protein": // TODO: This is better suited for the zombie gen
      FluidFuelRegister.instance.addFuel(event.getFluidName(), 25, 40000);
      break;
    default:
      return;
    }
    Log.info("Fuel Integration: Integration for fluid '" + event.getFluidName() + "' loaded");
  }

}
