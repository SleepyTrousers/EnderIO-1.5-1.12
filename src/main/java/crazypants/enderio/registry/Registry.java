package crazypants.enderio.registry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import crazypants.enderio.conduit.registry.ConduitRegistry;
import crazypants.enderio.init.IModObject;
import crazypants.enderio.item.darksteel.upgrade.solar.SolarUpgradeManager;
import net.minecraft.block.Block;
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

  public static @Nullable Block getConduitBlock() {
    return ConduitRegistry.getConduitBlock();
  }

  public static void registerConduitBlock(@Nonnull IModObject block) {
    ConduitRegistry.registerConduitBlock(block);
  }

}
