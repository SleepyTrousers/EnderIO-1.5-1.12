package crazypants.enderio.base.config;

import java.io.File;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.event.ConfigFileChangedEvent;
import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.Log;
import crazypants.enderio.base.config.config.BaseConfig;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;

public final class Config {

  private static Configuration config;

  private static File configDirectory;

  public static @Nonnull File getConfigDirectory() {
    return NullHelper.notnull(configDirectory, "trying to access config before preInit");
  }

  public static @Nullable File getConfigDirectoryRaw() {
    return configDirectory;
  }

  public static void init(FMLPreInitializationEvent event) {
    MinecraftForge.EVENT_BUS.register(new Config());
    configDirectory = new File(event.getModConfigurationDirectory(), EnderIO.DOMAIN);
    if (!configDirectory.exists()) {
      configDirectory.mkdir();
    }

    File configFile = new File(configDirectory, "EnderIO.cfg");
    config = new Configuration(configFile);
    syncConfig();
  }

  private static void syncConfig() {
    try {
      BaseConfig.load();
      BaseConfig.F.setConfig(config);
    } catch (Exception e) {
      Log.error("EnderIO has a problem loading it's configuration");
      e.printStackTrace();
    } finally {
      if (config.hasChanged()) {
        config.save();
      }
    }
  }

  @SubscribeEvent
  public void onConfigChanged(OnConfigChangedEvent event) {
    if (event.getModID().equals(EnderIO.MODID)) {
      Log.info("Updating config...");
      syncConfig();
    }
  }

  @SubscribeEvent
  public void onConfigFileChanged(ConfigFileChangedEvent event) {
    if (event.getModID().equals(EnderIO.MODID)) {
      Log.info("Updating config...");
      config.load();
      syncConfig();
      event.setSuccessful();
    }
  }

  @SubscribeEvent
  public void onPlayerLoggon(PlayerLoggedInEvent evt) {
    if (EnderIO.VERSION.contains("-") || EnderIO.VERSION.contains("@")) { // e.g. 1.2.3-nightly
      evt.player.sendMessage(new TextComponentString(
          TextFormatting.DARK_RED + "This is an " + TextFormatting.BLACK + "Ender IO " + TextFormatting.DARK_RED + "development build!"));
      evt.player.sendMessage(new TextComponentString(TextFormatting.DARK_RED + "It may trash your world at any time!"));
      evt.player.sendMessage(new TextComponentString(TextFormatting.DARK_RED + "Do not use it for anything but testing!"));
      evt.player.sendMessage(new TextComponentString("You have been warned..."));
    }
  }

  public static @Nonnull Configuration getConfig() {
    return NullHelper.notnull(config, "trying to access config before preInit");
  }

}
