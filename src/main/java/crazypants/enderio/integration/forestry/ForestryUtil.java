package crazypants.enderio.integration.forestry;

import javax.annotation.Nonnull;

import crazypants.enderio.Log;
import crazypants.enderio.handler.darksteel.DarkSteelRecipeManager;
import net.minecraftforge.fml.common.Loader;

public class ForestryUtil {

  private ForestryUtil() {
  }

  public static void addForestry() {
    if (Loader.isModLoaded("forestry")) {
      ForestryFarmer.init();
      Log.info("Farming Station: Forestry fully loaded");
    } else {
      Log.info("Farming Station: Forestry not loaded");
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
