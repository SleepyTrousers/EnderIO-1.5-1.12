package crazypants.enderio.base.registry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.util.NNList;

import crazypants.enderio.base.conduit.registry.ConduitRegistry;
import crazypants.enderio.base.config.ValueFactory.IValue;
import crazypants.enderio.base.init.IModObject;
import crazypants.enderio.base.item.darksteel.upgrade.solar.SolarUpgradeManager;
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

  public static void enableSolarUpgrade(@Nonnull Item item, @Nonnull NNList<IValue<Integer>> levelCostList, @Nonnull NNList<IValue<Integer>> rfList) {
    SolarUpgradeManager.enableSolarUpgrade(item, levelCostList, rfList);
  }

  public static @Nullable Block getConduitBlock() {
    return ConduitRegistry.getConduitBlock();
  }

  public static void registerConduitBlock(@Nonnull IModObject.Registerable block) {
    ConduitRegistry.registerConduitBlock(block);
  }

}
