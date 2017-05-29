package crazypants.enderio.integration.gardencore;

import crazypants.enderio.Log;
import crazypants.enderio.farming.FarmersRegistry;
import crazypants.enderio.farming.fertilizer.Bonemeal;
import crazypants.enderio.farming.fertilizer.Fertilizer;

public class GardencoreUtil {

  private GardencoreUtil() {
  }

  public static void addGardencore() {
    final Bonemeal fertilizer = new Bonemeal(FarmersRegistry.findItem("gardencore", "compost_pile"));
    if (fertilizer.isValid()) {
      Fertilizer.registerFertilizer(fertilizer);
      Log.info("Farming Station: Gardencore integration loaded");
    } else {
      Log.info("Farming Station: Gardencore integration not loaded");
    }
  }

}
