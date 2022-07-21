package crazypants.enderio.conduit.oc;

import cpw.mods.fml.common.Loader;
import crazypants.enderio.config.Config;

public class OCUtil {

    private static boolean useCheckPerformed = false;
    private static boolean isOCConduitEnabled = false;

    public static boolean isOCEnabled() {
        if (!useCheckPerformed) {
            isOCConduitEnabled = Loader.isModLoaded("OpenComputers") && Config.enableOCConduits;
            useCheckPerformed = true;
        }
        return isOCConduitEnabled;
    }
}
