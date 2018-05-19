package crazypants.enderio.zoo.config;

import java.io.File;

import javax.annotation.Nullable;

import com.enderio.core.common.event.ConfigFileChangedEvent;
import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.Log;
import crazypants.enderio.zoo.EnderIOZoo;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber(modid = EnderIOZoo.MODID)
public class ConfigHandler {

  @SuppressWarnings("null")
  public static Configuration config;

  public static File getConfigDirectory() {
    return NullHelper.notnull(getConfigDirectoryRaw(), EnderIO.MOD_NAME + " configuration failed");
  }

  public static @Nullable File getConfigDirectoryRaw() {
    return crazypants.enderio.base.config.Config.configDirectory;
  }

  public static void init(FMLPreInitializationEvent event) {
    File configDirectory = getConfigDirectoryRaw();
    if (configDirectory == null) {
      Log.warn(EnderIOZoo.MOD_NAME + " was initialized before " + EnderIO.MOD_NAME + ". This should not happen.");
      crazypants.enderio.base.config.Config.init(event);
      configDirectory = getConfigDirectory();
    }

    File configFile = new File(configDirectory, EnderIOZoo.MODID + ".cfg");
    config = new Configuration(configFile);
    syncConfig(false);
  }

  public static void syncConfig(boolean load) {
    try {
      if (load) {
        config.load();
      }
      processConfig();
    } catch (Exception e) {
      Log.error(EnderIOZoo.MOD_NAME + " has a problem loading its configuration:");
      e.printStackTrace();
    } finally {
      if (config.hasChanged()) {
        config.save();
      }
    }
  }

  @SubscribeEvent
  public static void onConfigChanged(OnConfigChangedEvent event) {
    if (event.getModID().equals(EnderIOZoo.MODID) || event.getModID().equals(EnderIO.MODID)) {
      // need to listen for EnderIO.MODID, too, as our config gets chained to the root mod for the GUI config
      Log.info("Updating config...");
      syncConfig(false);
    }
  }

  @SubscribeEvent
  public static void onConfigFileChanged(ConfigFileChangedEvent event) {
    if (event.getModID().equals(EnderIOZoo.MODID)) {
      Log.info("Updating config...");
      syncConfig(true);
      event.setSuccessful();
      // TODO: if this is a server we should re-send the config sync packet to all connected players
    }
  }

  private static void processConfig() {
    Config.load();
    Config.F.setConfig(config);
  }

}
