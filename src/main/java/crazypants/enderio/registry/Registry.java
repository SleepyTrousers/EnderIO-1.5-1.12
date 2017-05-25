package crazypants.enderio.registry;

import javax.annotation.Nonnull;

import crazypants.enderio.item.darksteel.upgrade.solar.SolarUpgradeManager;
import net.minecraft.item.Item;

/**
 * Central registry dispatcher for sub mods.
 *
 */
public final class Registry {

  private Registry() {
  }

  public static void registerRecipeFile(@Nonnull String filename) {
    // ...
  }

  public static void enableSolarUpgrade(@Nonnull Item item, int[] levelCostList, int[] rfList) {
    SolarUpgradeManager.enableSolarUpgrade(item, levelCostList, rfList);
  }

}
