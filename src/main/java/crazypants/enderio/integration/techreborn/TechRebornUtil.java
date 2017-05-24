package crazypants.enderio.integration.techreborn;

import crazypants.enderio.Log;
import crazypants.enderio.farming.farmers.FarmersCommune;
import crazypants.enderio.farming.farmers.RubberTreeFarmer;

public class TechRebornUtil {

  private TechRebornUtil() {
  }

  public static void addTechreborn() {
    RubberTreeFarmer farmer = RubberTreeFarmerTechReborn.create();
    if (farmer != null) {
      FarmersCommune.joinCommune(farmer);
      Log.info("Farming Station: TechReborn integration fully loaded");
    } else {
      Log.info("Farming Station: TechReborn integration not loaded");
    }
  }

}
