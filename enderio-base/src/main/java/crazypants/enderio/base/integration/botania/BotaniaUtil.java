package crazypants.enderio.base.integration.botania;

import java.lang.reflect.Method;

import javax.annotation.Nonnull;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.Log;
import crazypants.enderio.base.farming.FarmersRegistry;
import crazypants.enderio.base.farming.farmers.IFarmerJoe;
import crazypants.enderio.base.farming.farmers.PlaceableFarmer;
import crazypants.enderio.base.farming.fertilizer.IFertilizer;
import net.minecraft.entity.Entity;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber(modid = EnderIO.MODID)
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

  @SubscribeEvent(priority = EventPriority.NORMAL)
  public static void registerFarmers(@Nonnull RegistryEvent.Register<IFarmerJoe> event) {
    FarmersRegistry.registerFlower("block:botania:flower", "block:botania:doubleflower1", "block:botania:doubleflower2", "block:botania:shinyflower",
        "block:botania:mushroom");
    PlaceableFarmer farmer = new PlaceableFarmer("item:botania:petal");
    farmer.addDirt("block:minecraft:grass");
    if (farmer.isValid()) {
      event.getRegistry().register(farmer.setRegistryName("botania", "petals"));
      Log.info("Farming Station: Botania integration for farming fully loaded");
    } else {
      Log.info("Farming Station: Botania integration for farming not loaded");
    }
  }

  @SubscribeEvent
  public static void registerFertilizer(@Nonnull RegistryEvent.Register<IFertilizer> event) {
    final MagicalFertilizer fertilizer = new MagicalFertilizer(FarmersRegistry.findItem("botania", "fertilizer"));
    if (fertilizer.isValid()) {
      event.getRegistry().register(fertilizer);
      Log.info("Farming Station: Botania integration for fertilizing fully loaded");
    } else {
      Log.info("Farming Station: Botania integration for fertilizing not loaded");
    }
  }

}
