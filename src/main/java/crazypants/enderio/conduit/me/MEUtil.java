package crazypants.enderio.conduit.me;

import cpw.mods.fml.common.Loader;

public class MEUtil {

  public static boolean isMEEnabled() {
    return Loader.isModLoaded("appliedenergistics2");
  }

}
