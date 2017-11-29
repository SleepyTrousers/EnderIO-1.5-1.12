package crazypants.enderio.base.config;

import java.util.ArrayList;
import java.util.List;

import crazypants.enderio.base.EnderIO;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;

import static crazypants.enderio.base.config.Config.config;

public class GuiConfigFactoryEIO extends GuiConfig {

  public GuiConfigFactoryEIO(GuiScreen parentScreen) {
    super(parentScreen, getConfigElements(parentScreen), EnderIO.MODID, false, false, EnderIO.lang.localize("config.title"));
  }

  private static List<IConfigElement> getConfigElements(GuiScreen parent) {
    List<IConfigElement> list = new ArrayList<IConfigElement>();
    String prefix = EnderIO.lang.addPrefix("config.");

    for (String section : config.getCategoryNames()) {
      list.add(new ConfigElement(config.getCategory(section).setLanguageKey(prefix + section)));
    }

    return list;
  }
}
