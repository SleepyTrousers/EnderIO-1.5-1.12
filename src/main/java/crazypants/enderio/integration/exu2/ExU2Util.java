package crazypants.enderio.integration.exu2;

import crazypants.enderio.Log;
import crazypants.enderio.farming.FarmersRegistry;
import crazypants.enderio.farming.farmers.CustomSeedFarmer;
import net.minecraft.init.Blocks;

public class ExU2Util {

  private ExU2Util() {
  }

  public static void addExtraUtilities2() {
    int count = 0;
    CustomSeedFarmer farmer = FarmersRegistry.addSeed("extrautils2", "enderlilly", "enderlilly");
    if (farmer != null) {
      farmer.setIgnoreGroundCanSustainCheck(true);
      farmer.setRequiresFarmland(false); // disables tilling
      farmer.setCheckGroundForFarmland(true); // extra check needed when not tilling
      farmer.clearTilledBlocks(); // remove farmland
      farmer.addTilledBlock(Blocks.DIRT);
      farmer.addTilledBlock(Blocks.GRASS);
      farmer.addTilledBlock(Blocks.END_STONE);
      count++;
    }
    farmer = FarmersRegistry.addSeed("extrautils2", "redorchid", "redorchid");
    if (farmer != null) {
      farmer.setIgnoreGroundCanSustainCheck(true);
      farmer.setRequiresFarmland(false); // disables tilling
      farmer.setCheckGroundForFarmland(true); // extra check needed when not tilling
      farmer.clearTilledBlocks(); // remove farmland
      farmer.addTilledBlock(Blocks.REDSTONE_ORE);
      farmer.addTilledBlock(Blocks.LIT_REDSTONE_ORE);
      count++;
    }

    if (count == 2) {
      Log.info("Farming Station: Extra Utilities 2 integration fully loaded");
    } else if (count == 0) {
      Log.info("Farming Station: Extra Utilities 2 integration not loaded");
    } else {
      Log.info("Farming Station: Extra Utilities 2 integration partially loaded (" + count + " of 2)");
    }

  }

}
