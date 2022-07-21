package crazypants.util;

import crazypants.enderio.Log;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;

/**
 * This is a near-verbatim copy of Galacticraft's API class 'AccessInventoryGC'.
 *
 * We don't need the extended functionality and already have more APIs included
 * than is good for our sanity.
 *
 */
public class GalacticraftUtil {

    private static Class<?> playerStatsClass;
    private static Method getMethod;
    private static Field extendedInventoryField;

    private static boolean accessFailed = false;

    public static IInventory getGCInventoryForPlayer(EntityPlayerMP player) {
        if (!accessFailed) {
            try {
                if (playerStatsClass == null || getMethod == null || extendedInventoryField == null) {
                    playerStatsClass = Class.forName("micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStats");
                    getMethod = playerStatsClass.getMethod("get", EntityPlayerMP.class);
                    extendedInventoryField = playerStatsClass.getField("extendedInventory");
                }
                Object stats = getMethod.invoke(null, player);
                return stats == null ? null : (IInventory) extendedInventoryField.get(stats);
            } catch (Exception e) {
                Log.info("Galacticraft inventory inaccessable. Most likely because it is not installed.");
                accessFailed = true;
            }
        }

        return null;
    }
}
