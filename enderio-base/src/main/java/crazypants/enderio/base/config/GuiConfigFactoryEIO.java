package crazypants.enderio.base.config;

import java.util.ArrayList;
import java.util.List;

import crazypants.enderio.api.addon.IEnderIOAddon;
import crazypants.enderio.base.EnderIO;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.config.DummyConfigElement.DummyCategoryElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;

import static crazypants.enderio.base.lang.Lang.CONFIG_TITLE;

public class GuiConfigFactoryEIO extends GuiConfig {

  public GuiConfigFactoryEIO(GuiScreen parentScreen) {
    super(parentScreen, getConfigElements(parentScreen), EnderIO.MODID, false, false, CONFIG_TITLE.get());
  }

  private static List<IConfigElement> getConfigElements(GuiScreen parent) {
    List<IConfigElement> result = new ArrayList<>();
    List<ModContainer> modList = Loader.instance().getModList();
    for (ModContainer modContainer : modList) {
      Object mod = modContainer.getMod();
      if (mod instanceof IEnderIOAddon) {
        Configuration configuration = ((IEnderIOAddon) mod).getConfiguration();
        if (configuration != null) {
          List<IConfigElement> list = new ArrayList<>();
          for (String section : configuration.getCategoryNames()) {
            list.add(new ConfigElement(configuration.getCategory(section).setLanguageKey(EnderIO.lang.addPrefix("config." + section))));
          }
          result.add(new DummyCategoryElement(modContainer.getName(), EnderIO.lang.addPrefix("config.title." + modContainer.getModId()), list));
        }
      }
    }

    return result;
  }
}
