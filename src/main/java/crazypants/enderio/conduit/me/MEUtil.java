package crazypants.enderio.conduit.me;

import cpw.mods.fml.common.Loader;
import crazypants.enderio.config.Config;

public class MEUtil {

  private static boolean useCheckPerformed = false;
  private static boolean isMeConduitEnabled = false;

  public static boolean isMEEnabled() {
    if(!useCheckPerformed) {
      String configOption = Config.isMeConduitEnabled;
      if(configOption.equalsIgnoreCase("auto")) {
        isMeConduitEnabled = Loader.isModLoaded("appliedenergistics2");
      } else if(configOption.equalsIgnoreCase("true")) {
        isMeConduitEnabled = true;
      } else {
        isMeConduitEnabled = false;
      }
      useCheckPerformed = true;
    }
    return isMeConduitEnabled;
  }
}
