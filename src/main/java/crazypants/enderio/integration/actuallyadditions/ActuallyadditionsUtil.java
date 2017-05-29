package crazypants.enderio.integration.actuallyadditions;

import crazypants.enderio.Log;
import crazypants.enderio.farming.FarmersRegistry;
import crazypants.enderio.farming.fertilizer.Bonemeal;
import crazypants.enderio.farming.fertilizer.Fertilizer;

public class ActuallyadditionsUtil {

  private ActuallyadditionsUtil() {
  }

  public static void addActuallyadditions() {
    final Bonemeal fertilizer = new Bonemeal(FarmersRegistry.findItem("actuallyadditions", "item_fertilizer"));
    if (fertilizer.isValid()) {
      Fertilizer.registerFertilizer(fertilizer);
      Log.info("Farming Station: Actually Additions integration loaded");
    } else {
      Log.info("Farming Station: Actually Additions integration not loaded");
    }
  }

}
