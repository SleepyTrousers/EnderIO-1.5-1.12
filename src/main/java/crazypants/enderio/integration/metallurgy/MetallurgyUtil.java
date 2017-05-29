package crazypants.enderio.integration.metallurgy;

import crazypants.enderio.Log;
import crazypants.enderio.farming.FarmersRegistry;
import crazypants.enderio.farming.fertilizer.Bonemeal;
import crazypants.enderio.farming.fertilizer.Fertilizer;

public class MetallurgyUtil {

  private MetallurgyUtil() {
  }

  public static void addMetallurgy() {
    final Bonemeal fertilizer = new Bonemeal(FarmersRegistry.findItem("metallurgy", "fertilizer"));
    if (fertilizer.isValid()) {
      Fertilizer.registerFertilizer(fertilizer);
      Log.info("Farming Station: Gardencore integration loaded");
    } else {
      Log.info("Farming Station: Gardencore integration not loaded");
    }
  }

}
