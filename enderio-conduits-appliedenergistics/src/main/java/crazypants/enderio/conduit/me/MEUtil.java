package crazypants.enderio.conduit.me;

import net.minecraftforge.fml.common.Loader;

public class MEUtil {

  private static boolean useCheckPerformed = false;
  private static boolean isMeConduitEnabled = false;

  public static boolean isMEEnabled() {
    if (!useCheckPerformed) {
      isMeConduitEnabled = Loader.isModLoaded("appliedenergistics2");
      useCheckPerformed = true;
    }
    return isMeConduitEnabled;
  }
}
