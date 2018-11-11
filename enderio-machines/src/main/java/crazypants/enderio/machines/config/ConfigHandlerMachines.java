package crazypants.enderio.machines.config;

import javax.annotation.Nonnull;

import com.enderio.core.common.event.ConfigFileChangedEvent;

import crazypants.enderio.base.Log;
import info.loenwind.autoconfig.ConfigHandler;
import info.loenwind.autoconfig.factory.IRootFactory;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ConfigHandlerMachines extends ConfigHandler {

  public ConfigHandlerMachines(@Nonnull FMLPreInitializationEvent event, @Nonnull IRootFactory factory, String folder) {
    super(event, factory, folder);
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
