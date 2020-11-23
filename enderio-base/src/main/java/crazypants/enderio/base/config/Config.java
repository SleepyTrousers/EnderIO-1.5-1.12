package crazypants.enderio.base.config;

import java.io.File;

import javax.annotation.Nonnull;

import com.enderio.core.common.event.ConfigFileChangedEvent;

import crazypants.enderio.api.addon.IEnderIOAddon;
import crazypants.enderio.base.Log;
import info.loenwind.autoconfig.ConfigHandler;
import info.loenwind.autoconfig.factory.IRootFactory;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public final class Config extends ConfigHandler {

  public Config(@Nonnull FMLPreInitializationEvent event, @Nonnull IRootFactory factory, String folder) {
    super(event, factory, folder);
  }

  // endercore config file reload event
  @SubscribeEvent
  public void onConfigFileChanged(ConfigFileChangedEvent event) {
    if (event.getModID().equals(factory.getModid())) {
      Log.info("Reloading config file...");
      config.load();
      syncConfig();
      event.setSuccessful();
      Log.info("Config reload finished");
    }
  }

  @Override
  @SubscribeEvent
  public void onConfigChanged(@Nonnull OnConfigChangedEvent event) {
    if (event.getModID().equals(factory.getModid())) {
      Log.info("Updating config...");
      syncConfig();

      // also notify addons of the config change as we present their config values in our GUI
      for (ModContainer modContainer : Loader.instance().getModList()) {
        if (modContainer.getMod() instanceof IEnderIOAddon && !modContainer.getModId().equals(factory.getModid())) {
          MinecraftForge.EVENT_BUS.post(new OnConfigChangedEvent(modContainer.getModId(), null, event.isWorldRunning(), event.isRequiresMcRestart()));
        }
      }

      Log.info("Config update finished");
    }
  }

  public @Nonnull File getConfigDirectory() {
    return configDirectory;
  }

}
