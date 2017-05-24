package crazypants.enderio.integration.botania;

import java.lang.reflect.Method;

import crazypants.enderio.Log;
import crazypants.enderio.farming.FarmersRegistry;
import crazypants.enderio.farming.farmers.FarmersCommune;
import crazypants.enderio.farming.farmers.PlaceableFarmer;
import net.minecraft.entity.Entity;

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
        Log.debug("Found Botania's Solegnolia class. Magnet will not be greedy.");
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
          Log.warn("Failed to interact with Botania too often. Magnet will ignore Solegnolias from now on. Last error was: " + t);
          hasSolegnoliaAround = null;
        }
      }
    }
    return false;
  }

  public static void addBotania() {
    FarmersRegistry.registerFlower("block:botania:flower", "block:botania:doubleflower1", "block:botania:doubleflower2", "block:botania:shinyflower",
        "block:botania:mushroom");
    PlaceableFarmer farmer = new PlaceableFarmer("item:botania:petal");
    farmer.addDirt("block:minecraft:grass");
    if (farmer.isValid()) {
      FarmersCommune.joinCommune(farmer);
    }
  }

}
