package crazypants.enderio.base.config;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

import crazypants.enderio.api.addon.IEnderIOAddon;
import crazypants.enderio.base.EnderIO;
import info.loenwind.autoconfig.gui.ConfigFactory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;

import static crazypants.enderio.base.lang.Lang.CONFIG_TITLE;

public class GUIFactory extends ConfigFactory {

  @Override
  protected @Nonnull String getModID() {
    return EnderIO.MODID;
  }

  @Override
  protected @Nonnull String getTitle() {
    return CONFIG_TITLE.get();
  }

  @Override
  protected @Nonnull String getTitle2() {
    return "";
  }

  @Override
  protected @Nonnull Map<String, Configuration> getConfigurations() {
    Map<String, Configuration> result = new HashMap<>();
    for (ModContainer modContainer : Loader.instance().getModList()) {
      Object mod = modContainer.getMod();
      if (mod instanceof IEnderIOAddon) {
        Configuration configuration = ((IEnderIOAddon) mod).getConfiguration();
        if (configuration != null) {
          result.put(modContainer.getModId(), configuration);
        }
      }
    }
    return result;
  }

}
