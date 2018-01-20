package crazypants.enderio.integration.tic;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.Lang;

import crazypants.enderio.api.addon.IEnderIOAddon;
import crazypants.enderio.base.Log;
import net.minecraft.block.Block;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.IForgeRegistry;

@Mod(modid = EnderIOIntegrationTic.MODID, name = EnderIOIntegrationTic.MOD_NAME, version = EnderIOIntegrationTic.VERSION, dependencies = EnderIOIntegrationTic.DEPENDENCIES)
public class EnderIOIntegrationTic implements IEnderIOAddon {

  public static final @Nonnull String MODID = "enderiointegrationtic";
  public static final @Nonnull String DOMAIN = "enderio";
  public static final @Nonnull String MOD_NAME = "Ender IO Integration TiC";
  public static final @Nonnull String VERSION = "@VERSION@";

  private static final @Nonnull String DEFAULT_DEPENDENCIES = "after:" + crazypants.enderio.base.EnderIO.MODID;
  public static final @Nonnull String DEPENDENCIES = DEFAULT_DEPENDENCIES;

  @EventHandler
  public static void init(FMLPreInitializationEvent event) {
    if (isLoaded()) {
      TicControl.init(event);
      Log.warn("TConstruct, you fail again, muhaha! The world is mine, mine!");
    } else {
      Log.warn("Tinkers' Construct integration NOT loaded. Tinkers' Construct is not installed");
    }
  }

  @EventHandler
  public static void init(FMLInitializationEvent event) {
    if (isLoaded()) {
      TicControl.init(event);
    }
  }

  @EventHandler
  public static void init(FMLPostInitializationEvent event) {
    if (isLoaded()) {
      TicControl.init(event);
    }
  }

  public static final @Nonnull Lang lang = new Lang(DOMAIN);

  @Override
  @Nullable
  public Configuration getConfiguration() {
    return null;
  }

  @Override
  public void injectBlocks(@Nonnull IForgeRegistry<Block> registry) {
    if (isLoaded()) {
      TicControl.injectBlocks(registry);
    }
  }

  public static boolean isLoaded() {
    return Loader.isModLoaded("tconstruct");
  }

}
