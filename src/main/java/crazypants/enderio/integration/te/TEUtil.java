package crazypants.enderio.integration.te;

import crazypants.enderio.Log;
import net.minecraftforge.fml.common.ModAPIManager;

public class TEUtil {

  public static void create() {
    if (ModAPIManager.INSTANCE.hasAPI("cofhapi|item")) {
      // Add support for TE wrench
      try {
        Class.forName("crazypants.enderio.integration.te.TEToolProvider").newInstance();
      } catch (Exception e) {
        Log.warn("Could not find Thermal Expansion Wrench definition. Wrench integration with it may fail");
      }
    }
  }

}
