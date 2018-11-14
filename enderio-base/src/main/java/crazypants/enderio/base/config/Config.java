package crazypants.enderio.base.config;

import java.io.File;

import javax.annotation.Nonnull;

import com.enderio.core.common.event.ConfigFileChangedEvent;

import crazypants.enderio.api.addon.IEnderIOAddon;
import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.Log;
import info.loenwind.autoconfig.ConfigHandler;
import info.loenwind.autoconfig.factory.IRootFactory;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;

public final class Config extends ConfigHandler {

  public Config(@Nonnull FMLPreInitializationEvent event, @Nonnull IRootFactory factory, String folder) {
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

    }
  }

  // this doesn't belong here...
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

  public @Nonnull File getConfigDirectory() {
    return configDirectory;
  }

}
