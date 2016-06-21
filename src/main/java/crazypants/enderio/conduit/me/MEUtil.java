package crazypants.enderio.conduit.me;

import cpw.mods.fml.common.Loader;
import crazypants.enderio.config.Config;

public class MEUtil {

  private static boolean useCheckPerformed = false;
  private static boolean isMeConduitEnabled = false;

  public static boolean isMEEnabled() {
    if(!useCheckPerformed) {
      isMeConduitEnabled = Loader.isModLoaded("appliedenergistics2") && Config.enableMEConduits;
      useCheckPerformed = true;
    }
    return isMeConduitEnabled;
  }
}
