package crazypants.enderio.integration.forestry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.Lang;

import crazypants.enderio.api.addon.IEnderIOAddon;
import crazypants.enderio.base.Log;
import crazypants.enderio.integration.forestry.config.ConfigHandler;
import net.minecraft.block.Block;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = EnderIOIntegrationForestry.MODID, name = EnderIOIntegrationForestry.MOD_NAME, version = EnderIOIntegrationForestry.VERSION, dependencies = EnderIOIntegrationForestry.DEPENDENCIES)
@EventBusSubscriber
public class EnderIOIntegrationForestry implements IEnderIOAddon {

  public static final @Nonnull String MODID = "enderiointegrationforestry";
  public static final @Nonnull String DOMAIN = "enderio";
  public static final @Nonnull String MOD_NAME = "Ender IO Integration with Forestry";
  public static final @Nonnull String VERSION = "@VERSION@";

  private static final @Nonnull String DEFAULT_DEPENDENCIES = "after:" + crazypants.enderio.base.EnderIO.MODID;
  public static final @Nonnull String DEPENDENCIES = DEFAULT_DEPENDENCIES;

  @EventHandler
  public static void init(@Nonnull FMLPreInitializationEvent event) {
    ConfigHandler.init(event);
    if (isLoaded()) {
      ForestryControl.init(event);
      Log.warn("Forestry integration loaded. Let things grow.");
    } else {
      Log.warn("Forestry integration NOT loaded. Forestry is not installed");
    }
  }

  @EventHandler
  public static void init(FMLInitializationEvent event) {
    if (isLoaded()) {
      ForestryControl.init(event);
    }
  }

  @EventHandler
  public static void init(FMLPostInitializationEvent event) {
    if (isLoaded()) {
      ForestryControl.init(event);
    }
  }

  public static final @Nonnull Lang lang = new Lang(DOMAIN);

  @Override
  @Nullable
  public Configuration getConfiguration() {
    return isLoaded() ? ConfigHandler.config : null;
  }

  @SubscribeEvent
  public static void registerFarmers(@Nonnull RegistryEvent.Register<Block> event) {
    // No blocks to register, but we need to be on the event bus during the registry events. And as Block is guaranteed to be first, this is the perfect place.
    if (isLoaded()) {
      ForestryControl.registerEventBus();
    }
  }

  public static boolean isLoaded() {
    return Loader.isModLoaded("forestry");
  }

}
