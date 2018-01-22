package crazypants.enderio.conduit;

import com.enderio.core.common.event.ConfigFileChangedEvent;
import com.enderio.core.common.util.NullHelper;
import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.Log;
import crazypants.enderio.powertools.config.Config;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nonnull;
import java.io.File;

@Mod.EventBusSubscriber(modid = EnderIOConduits.MODID)
public class ConfigHandler {

  public static Configuration config;

  public static @Nonnull File getConfigDirectory() {
    return NullHelper.notnull(getConfigDirectoryRaw(), EnderIO.MOD_NAME + " configuration failed");
  }

  public static File getConfigDirectoryRaw() {
    return crazypants.enderio.base.config.Config.configDirectory;
  }

  public static void init(FMLPreInitializationEvent event) {
//    File configDirectory = getConfigDirectoryRaw();
//    if (configDirectory == null) {
//      Log.warn(EnderIOConduits.MOD_NAME + " was initialized before " + EnderIO.MOD_NAME + ". This should not happen.");
//      crazypants.enderio.base.config.Config.init(event);
//      configDirectory = getConfigDirectory();
//    }
//
//    File configFile = new File(configDirectory, EnderIOConduits.MODID + ".cfg");
//    config = new Configuration(configFile);
//    syncConfig(false);
  }

  public static void init(FMLInitializationEvent event) {
  }

  public static void init(FMLPostInitializationEvent event) {
  }

  public static void syncConfig(boolean load) {
//    try {
//      if (load) {
//        config.load();
//      }
//      processConfig();
//    } catch (Exception e) {
//      Log.error(EnderIOConduits.MOD_NAME + " has a problem loading its configuration:");
//      e.printStackTrace();
//    } finally {
//      if (config.hasChanged()) {
//        config.save();
//      }
//    }
  }

  @SubscribeEvent
  public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
    if (event.getModID().equals(EnderIOConduits.MODID) || event.getModID().equals(EnderIO.MODID)) {
      // need to listen for EnderIO.MODID, too, as our config gets chained to the root mod for the GUI config
      Log.info("Updating config...");
      syncConfig(false);
      init((FMLInitializationEvent) null);
      init((FMLPostInitializationEvent) null);
    }
  }

  @SubscribeEvent
  public static void onConfigFileChanged(ConfigFileChangedEvent event) {
    if (event.getModID().equals(EnderIOConduits.MODID)) {
      Log.info("Updating config...");
      syncConfig(true);
      event.setSuccessful();
      init((FMLInitializationEvent) null);
      init((FMLPostInitializationEvent) null);
      // TODO: if this is a server we should re-send the config sync packet to all connected players
    }
  }

  private static void processConfig() {
    Config.load();
    Config.F.setConfig(config);
  }
}
