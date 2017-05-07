package crazypants.enderio.integration.forestry;

import crazypants.enderio.Log;
import net.minecraftforge.fml.common.Loader;

public class ForestryUtil {

  private ForestryUtil() {
  }

  public static void addForestry() {
    if (Loader.isModLoaded("forestry")) {
      ForestryFarmer.init();
      Log.info("Farming Station: Forestry fully loaded");
    } else {
      Log.info("Farming Station: Forestry not loaded");
    }
  }

}
