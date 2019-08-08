package crazypants.enderio.base.integration.galacticraft;

import java.lang.reflect.Method;

import crazypants.enderio.base.Log;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;

/**
 * This is a near-verbatim copy of Galacticraft's API class 'AccessInventoryGC'.
 * <p>
 * see <a href="https://github.com/micdoodle8/Galacticraft/blob/MC1.12/src/main/java/micdoodle8/mods/galacticraft/api/inventory/AccessInventoryGC.java">here</a>
 * <p>
 * We don't need the extended functionality and already have more APIs included than is good for our sanity.
 *
 */
public class GalacticraftUtil {

  private static Class<?> playerStatsClass;
  private static Method getStats;
  private static Method getExtendedInventory;

  private static boolean accessFailed = false;

  public static IInventory getGCInventoryForPlayer(EntityPlayerMP player) {
    if (!accessFailed) {
      try {
        if (playerStatsClass == null || getStats == null || getExtendedInventory == null) {
          playerStatsClass = Class.forName("micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStats");
          getStats = playerStatsClass.getMethod("get", Entity.class);
          getExtendedInventory = playerStatsClass.getMethod("getExtendedInventory");
        }

        Object stats = getStats.invoke(null, player);
        return stats == null ? null : (IInventory) getExtendedInventory.invoke(stats);
      } catch (Exception e) {
        Log.info("Galacticraft inventory inaccessable. Most likely because it is not installed.");
        accessFailed = true;
      }
    }

    return null;
  }
}