package crazypants.enderio.conduit.me;

import crazypants.enderio.config.Config;
import net.minecraftforge.fml.common.Loader;

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
