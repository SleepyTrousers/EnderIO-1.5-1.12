package crazypants.enderio.base.config;

import javax.annotation.Nonnull;

import com.enderio.core.common.event.ConfigFileChangedEvent;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.Log;
import crazypants.enderio.base.config.factory.ValueFactoryEIO;
import info.loenwind.autoconfig.ConfigHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * ConfigHandler for addon mods
 *
 */
public class ConfigHandlerEIO extends ConfigHandler {

  public ConfigHandlerEIO(@Nonnull FMLPreInitializationEvent event, @Nonnull ValueFactoryEIO factory) {
    super(event, factory, EnderIO.DOMAIN);
  }

  // endercore config file reload event
  @SubscribeEvent
  public void onConfigFileChanged(ConfigFileChangedEvent event) {
    if (event.getModID().equals(factory.getModid())) {
      Log.info("Updating config...");
      config.load();
      syncConfig();
      event.setSuccessful();
    }
  }

}
