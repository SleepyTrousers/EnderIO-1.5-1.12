package crazypants.enderio.integration.buildcraft;

import crazypants.enderio.Log;
import net.minecraftforge.fml.common.Loader;

public class BuildcraftIntegration {

  
  public static void init() {
    //Add support for buildcraft wrench
    try {
      Class.forName("crazypants.enderio.buildcraft.BuildCraftToolProvider").newInstance();               
    } catch (Exception e) {
      Log.warn("Could not find Build Craft Wrench definition. Wrench integration with other mods may fail");
    }
    
    //adds support for inserting into pipes
    try {
      Class.forName("crazypants.util.BuildcraftUtil");
    } catch(Exception e) {
      if (Loader.isModLoaded("BuildCraft|Transport")) {
        Log.warn("ItemUtil: Could not register Build Craft pipe handler. Machines will not be able to output to BC pipes.");
      } 
    }
    
    //Registers build craft fluids as fuels
    if(Loader.isModLoaded("BuildCraft|Energy")) {
      try {
        Class.forName("crazypants.enderio.buildcraft.BuildCraftFluidRegister").newInstance();        
      } catch (Exception e) {
        Log.error("FluidFuelRegister: Error occured registering build craft fuels: " + e);
      }
    }
    
  }
  
}
