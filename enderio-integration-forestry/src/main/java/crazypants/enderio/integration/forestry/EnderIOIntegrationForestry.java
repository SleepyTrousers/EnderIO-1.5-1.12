package crazypants.enderio.integration.forestry;

import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Triple;

import com.enderio.core.common.Lang;
import com.enderio.core.common.mixin.SimpleMixinLoader;
import com.enderio.core.common.util.NNList;

import crazypants.enderio.api.addon.IEnderIOAddon;
import crazypants.enderio.base.Log;
import crazypants.enderio.base.config.ConfigHandlerEIO;
import crazypants.enderio.base.config.recipes.RecipeFactory;
import crazypants.enderio.base.init.RegisterModObject;
import crazypants.enderio.integration.forestry.config.Config;
import crazypants.enderio.integration.forestry.init.ForestryIntegrationObject;
import info.loenwind.autoconfig.ConfigHandler;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkCheckHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod(modid = EnderIOIntegrationForestry.MODID, name = EnderIOIntegrationForestry.MOD_NAME, version = EnderIOIntegrationForestry.VERSION, dependencies = EnderIOIntegrationForestry.DEPENDENCIES)
@EventBusSubscriber
public class EnderIOIntegrationForestry implements IEnderIOAddon {

  @NetworkCheckHandler
  @SideOnly(Side.CLIENT)
  public boolean checkModLists(Map<String, String> modList, Side side) {
    /*
     * On the client when showing the server list: Require the mod to be there and of the same version.
     * 
     * On the client when connecting to a server: Require the mod to be there. Version check is done on the server.
     * 
     * On the server when a client connects: Standard Forge version checks with a nice error message apply.
     * 
     * On the integrated server when a client connects: Require the mod to be there and of the same version. Ugly error message.
     */
    return modList.keySet().contains(MODID) && VERSION.equals(modList.get(MODID));
  }

  public static final @Nonnull String MODID = "enderiointegrationforestry";
  public static final @Nonnull String DOMAIN = "enderio";
  public static final @Nonnull String MOD_NAME = "Ender IO Integration with Forestry";
  public static final @Nonnull String VERSION = "@VERSION@";

  private static final @Nonnull String DEFAULT_DEPENDENCIES = "after:" + crazypants.enderio.base.EnderIO.MODID;
  public static final @Nonnull String DEPENDENCIES = DEFAULT_DEPENDENCIES;

  @SuppressWarnings("unused")
  private static ConfigHandler configHandler;

  public EnderIOIntegrationForestry() {
    SimpleMixinLoader.loadMixinSources(this);
  }

  @EventHandler
  public static void init(@Nonnull FMLPreInitializationEvent event) {
    configHandler = new ConfigHandlerEIO(event, Config.F);
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
    return isLoaded() ? Config.F.getConfig() : null;
  }

  @SubscribeEvent
  public static void registerFarmers(@Nonnull RegisterModObject event) {
    // No blocks to register, but we need to be on the event bus during the registry events. And as Block is guaranteed to be first, this is the perfect place.
    if (isLoaded()) {
      ForestryIntegrationObject.registerBlocksEarly(event);
      ForestryControl.registerEventBus();
    }
  }

  @Override
  @Nonnull
  public NNList<Triple<Integer, RecipeFactory, String>> getRecipeFiles() {
    if (isLoaded()) {
      return new NNList<>(Triple.of(2, null, "integration-forestry"), Triple.of(2, null, "darksteel_upgrades_forestry"));
    }
    return NNList.emptyList();
  }

  public static boolean isLoaded() {
    return Loader.isModLoaded("forestry");
  }

}
