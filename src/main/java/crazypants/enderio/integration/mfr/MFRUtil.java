package crazypants.enderio.integration.mfr;

import crazypants.enderio.Log;
import crazypants.enderio.machine.farm.FarmersRegistry;
import crazypants.enderio.machine.farm.farmers.FarmersCommune;
import crazypants.enderio.machine.farm.farmers.TreeFarmer;
import net.minecraft.block.Block;

public class MFRUtil {

  private MFRUtil() {
  }

  public static void addMFR() {
    Block cropBlock = FarmersRegistry.findBlock("MineFactoryReloaded", "rubberwood.sapling");
    Block woodBlock = FarmersRegistry.findBlock("MineFactoryReloaded", "rubberwood.log");
    if (cropBlock != null && woodBlock != null) {
      FarmersCommune.joinCommune(new TreeFarmer(cropBlock, woodBlock));
      Log.info("Farming Station: MFR integration fully loaded");
    } else {
      Log.info("Farming Station: MFR integration not loaded");
    }
  }

}
