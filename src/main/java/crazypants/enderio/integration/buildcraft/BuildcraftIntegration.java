package crazypants.enderio.integration.buildcraft;

import javax.annotation.Nonnull;

import crazypants.enderio.Log;
import crazypants.enderio.fluid.FluidFuelRegister;
import crazypants.enderio.fluid.IFluidRegister;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;

public class BuildcraftIntegration {

  public static void init(@Nonnull FMLPostInitializationEvent event) {
    // Add support for buildcraft wrench
    try {
      Class.forName("crazypants.enderio.integration.buildcraft.BuildCraftToolProvider").newInstance();
    } catch (Exception e) {
      Log.warn("Could not find Build Craft Wrench definition. Wrench integration with other mods may fail");
    }

    // adds support for inserting into pipes
    if (Loader.isModLoaded("BuildCraft|Transport")) {
      try {
        Class.forName("crazypants.enderio.integration.buildcraft.BuildcraftUtil");
      } catch (Exception e) {
        Log.warn("ItemUtil: Could not register Build Craft pipe handler. Machines will not be able to output to BC pipes.");
      }
    }

    // Registers build craft fluids as fuels
    if (Loader.isModLoaded("BuildCraft|Energy")) {
      try {
        FluidFuelRegister.instance
            .addRegister((IFluidRegister) Class.forName("crazypants.enderio.integration.buildcraft.BuildCraftFluidRegister").newInstance());
      } catch (Exception e) {
        Log.error("FluidFuelRegister: Error occured registering build craft fuels: " + e);
      }
    }

  }

}
