package crazypants.enderio.integration.magicalcrops;

import crazypants.enderio.Log;
import crazypants.enderio.farming.FarmersRegistry;
import crazypants.enderio.farming.fertilizer.Bonemeal;
import crazypants.enderio.farming.fertilizer.Fertilizer;

public class MagicalcropsUtil {

  private MagicalcropsUtil() {
  }

  public static void addMagicalcrops() {
    final Bonemeal fertilizer = new Bonemeal(FarmersRegistry.findItem("magicalcrops", "magicalcrops_magicalcropfertilizer"));
    if (fertilizer.isValid()) {
      Fertilizer.registerFertilizer(fertilizer);
      Log.info("Farming Station: Magicalcrops integration loaded");
    } else {
      Log.info("Farming Station: Magicalcrops integration not loaded");
    }
  }

}
