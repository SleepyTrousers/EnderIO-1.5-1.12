package crazypants.enderio.integration.immersiveengineering;

import crazypants.enderio.Log;
import crazypants.enderio.farming.farmers.FarmersCommune;

public class ImmersiveEngineeringUtil {

  private ImmersiveEngineeringUtil() {
  }

  public static void addImmersiveEngineering() {
    HempFarmerIE farmer = HempFarmerIE.create();
    if (farmer != null) {
      FarmersCommune.joinCommune(farmer);
      Log.info("Farming Station: Immersive Engineering integration fully loaded");
    } else {
      Log.info("Farming Station: Immersive Engineering integration not loaded");
    }
  }

}
