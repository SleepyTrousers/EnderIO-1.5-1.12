package crazypants.enderio.machines.config;

import java.io.File;

import com.enderio.core.common.event.ConfigFileChangedEvent;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.Log;
import crazypants.enderio.machines.EnderIOMachines;
import crazypants.enderio.machines.capacitor.CapacitorKey;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber(modid = EnderIOMachines.MODID)
public class ConfigHandler {

  public static Configuration config;

  public static File getConfigDirectory() {
    return crazypants.enderio.base.config.Config.configDirectory;
  }

  public static void init(FMLPreInitializationEvent event) {
    File configDirectory = getConfigDirectory();
    if (configDirectory == null) {
      Log.warn(EnderIOMachines.MOD_NAME + " was initialized before " + EnderIO.MOD_NAME + ". This should not happen.");
      crazypants.enderio.base.config.Config.init(event);
      configDirectory = getConfigDirectory();
    }

    File configFile = new File(configDirectory, EnderIOMachines.MODID + ".cfg");
    config = new Configuration(configFile);
    syncConfig(false);
  }

  public static void init(FMLInitializationEvent event) {
  }

  public static void init(FMLPostInitializationEvent event) {
  }

  public static void syncConfig(boolean load) {
    try {
      if (load) {
        config.load();
      }
      processConfig();
    } catch (Exception e) {
      Log.error(EnderIOMachines.MOD_NAME + " has a problem loading its configuration:");
      e.printStackTrace();
    } finally {
      if (config.hasChanged()) {
        config.save();
      }
    }
  }

  @SubscribeEvent
  public static void onConfigChanged(OnConfigChangedEvent event) {
    if (event.getModID().equals(EnderIOMachines.MODID)) {
      Log.info("Updating config...");
      syncConfig(false);
      init((FMLInitializationEvent) null);
      init((FMLPostInitializationEvent) null);
    }
  }

  @SubscribeEvent
  public static void onConfigFileChanged(ConfigFileChangedEvent event) {
    if (event.getModID().equals(EnderIOMachines.MODID)) {
      Log.info("Updating config...");
      syncConfig(true);
      event.setSuccessful();
      init((FMLInitializationEvent) null);
      init((FMLPostInitializationEvent) null);
    }
  }

  private static void processConfig() {
    Config.F.setConfig(config);

    CapacitorKey.processConfig(config);
  }

}
