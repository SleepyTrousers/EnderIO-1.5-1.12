package crazypants.enderio.base.config;

import java.util.ArrayList;
import java.util.List;

import crazypants.enderio.api.addon.IEnderIOAddon;
import crazypants.enderio.base.EnderIO;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigCategory;
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

  /*
   * Note: We are using the Forge config structure to build our GUI. This makes our ConfigElementEio a bit hacky, so it would be better to keep our own tree in
   * ValueFactory.
   */

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
            final ConfigCategory category = configuration.getCategory(section);
            category.setLanguageKey(EnderIO.lang.addPrefix("config." + category.getQualifiedName()));
            if (!category.isChild()) {
              list.add(new ConfigElementEio(category));
            }
          }
          result.add(new DummyCategoryElement(modContainer.getName(), EnderIO.lang.addPrefix("config.title." + modContainer.getModId()), list));
        }
      }
    }

    return result;
  }
}
