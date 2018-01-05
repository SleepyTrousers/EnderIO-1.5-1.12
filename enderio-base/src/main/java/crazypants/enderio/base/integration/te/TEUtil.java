package crazypants.enderio.base.integration.te;

import javax.annotation.Nonnull;

import crazypants.enderio.base.Log;
import net.minecraftforge.fml.common.ModAPIManager;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;

public class TEUtil {

  public static void init(@Nonnull FMLPostInitializationEvent event) {
    if (ModAPIManager.INSTANCE.hasAPI("cofhapi|item")) {
      // Add support for TE wrench
      try {
        Class.forName("crazypants.enderio.base.integration.te.TEToolProvider").newInstance();
      } catch (Exception e) {
        Log.warn("Could not find Thermal Expansion Wrench definition. Wrench integration with it may fail");
      }
    }
  }

}
