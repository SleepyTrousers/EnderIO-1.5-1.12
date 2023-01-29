package crazypants.util;

import java.lang.reflect.Method;

import net.minecraft.entity.Entity;

import crazypants.enderio.Log;

public class BotaniaUtil {

    private static final String SOLEGNOLIA = "vazkii.botania.common.block.subtile.functional.SubTileSolegnolia";
    private static final String HAS_SOLEGNOLIA_AROUND = "hasSolegnoliaAround";
    private static Method hasSolegnoliaAround = null;
    private static int errorCount = -1;

    public static boolean hasSolegnoliaAround(Entity entity) {
        if (errorCount < 0) {
            errorCount = 0;
            try {
                Class<?> solegnolia = Class.forName(SOLEGNOLIA);
                hasSolegnoliaAround = solegnolia.getMethod(HAS_SOLEGNOLIA_AROUND, Entity.class);
            } catch (Throwable t) {
                Log.debug("Didn't find Botania's Solegnolia class. Magnet will be greedy.");
            }
        }
        if (hasSolegnoliaAround != null) {
            try {
                Boolean result = (Boolean) hasSolegnoliaAround.invoke(null, entity);
                if (errorCount > 0) {
                    errorCount--;
                }
                return result;
            } catch (Throwable t) {
                if (errorCount++ > 10) {
                    Log.warn(
                            "Failed to interact with Botania too often. Magnet will ignore Solegnolias from now on. Last error was: "
                                    + t);
                    hasSolegnoliaAround = null;
                }
            }
        }
        return false;
    }
}
