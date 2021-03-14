package crazypants.enderio.base.registry;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import crazypants.enderio.api.IModObject;
import crazypants.enderio.api.addon.IEnderIOAddon;
import crazypants.enderio.base.conduit.registry.ConduitRegistry;
import net.minecraft.block.Block;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;

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

  public static @Nullable Block getConduitBlock() {
    return ConduitRegistry.getConduitBlock();
  }

  public static void registerConduitBlock(@Nonnull IModObject block) {
    ConduitRegistry.registerConduitBlock(block);
  }

  public static @Nonnull Map<String, Configuration> getConfigurations() {
    Map<String, Configuration> result = new HashMap<>();
    for (ModContainer modContainer : Loader.instance().getModList()) {
      Object mod = modContainer.getMod();
      if (mod instanceof IEnderIOAddon) {
        Configuration configuration = ((IEnderIOAddon) mod).getConfiguration();
        if (configuration != null) {
          result.put(modContainer.getModId(), configuration);
        }
      }
    }
    return result;
  }

}
