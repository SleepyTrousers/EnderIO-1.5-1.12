package crazypants.enderio.conduit.me;

import cpw.mods.fml.common.Loader;
import crazypants.enderio.config.Config;

public class MEUtil {

  private static boolean useCheckPerformed = false;
  private static boolean isGasConduitEnabled = false;

  public static boolean isMEEnabled() {
    if(!useCheckPerformed) {
      String configOption = Config.isGasConduitEnabled;
      if(configOption.equalsIgnoreCase("auto")) {
        isGasConduitEnabled = Loader.isModLoaded("appliedenergistics2");
      } else if(configOption.equalsIgnoreCase("true")) {
        isGasConduitEnabled = true;
      } else {
        isGasConduitEnabled = false;
      }
      useCheckPerformed = true;
    }
    return isGasConduitEnabled;
  }
}
