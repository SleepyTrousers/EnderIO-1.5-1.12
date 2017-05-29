package crazypants.enderio.integration.forestry;

import javax.annotation.Nonnull;

import crazypants.enderio.Log;
import crazypants.enderio.farming.FarmersRegistry;
import crazypants.enderio.farming.fertilizer.Bonemeal;
import crazypants.enderio.farming.fertilizer.Fertilizer;
import crazypants.enderio.handler.darksteel.DarkSteelRecipeManager;
import net.minecraftforge.fml.common.Loader;

public class ForestryUtil {

  private ForestryUtil() {
  }

  public static void addForestry() {
    if (Loader.isModLoaded("forestry")) {
      ForestryFarmer.init();
      Fertilizer.registerFertilizer(new Bonemeal(FarmersRegistry.findItem("forestry", "fertilizer_compound")));
      Log.info("Farming Station: Forestry integration fully loaded");
    } else {
      Log.info("Farming Station: Forestry integration not loaded");
    }
  }

  public static void addUpgrades(@Nonnull DarkSteelRecipeManager manager) {
    if (Loader.isModLoaded("forestry")) {
      manager.addUpgrade(NaturalistEyeUpgrade.INSTANCE);
      manager.addUpgrade(ApiaristArmorUpgrade.HELMET);
      manager.addUpgrade(ApiaristArmorUpgrade.CHEST);
      manager.addUpgrade(ApiaristArmorUpgrade.LEGS);
      manager.addUpgrade(ApiaristArmorUpgrade.BOOTS);
    }
  }

}
