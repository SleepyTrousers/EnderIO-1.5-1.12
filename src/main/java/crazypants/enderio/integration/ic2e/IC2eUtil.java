package crazypants.enderio.integration.ic2e;

import crazypants.enderio.Log;
import crazypants.enderio.machine.farm.farmers.FarmersCommune;
import crazypants.enderio.machine.farm.farmers.RubberTreeFarmer;

public class IC2eUtil {

  private IC2eUtil() {
  }

  public static void addIC2() {
    RubberTreeFarmer farmer = RubberTreeFarmerIC2exp.create();
    if (farmer != null) {
      FarmersCommune.joinCommune(farmer);
      Log.info("Farming Station: IC2 integration fully loaded");
    } else {
      Log.info("Farming Station: IC2 integration not loaded");
    }
  }

}
