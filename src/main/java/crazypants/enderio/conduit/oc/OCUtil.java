package crazypants.enderio.conduit.oc;

import crazypants.enderio.config.Config;
import net.minecraftforge.fml.common.Loader;

public class OCUtil {

  private static boolean useCheckPerformed = false;
  private static boolean isOCConduitEnabled = false;

  public static boolean isOCEnabled() {
    if(!useCheckPerformed) {
      isOCConduitEnabled = Loader.isModLoaded("OpenComputers") && Config.enableOCConduits;
      useCheckPerformed = true;
    }
    return isOCConduitEnabled;
  }
}
