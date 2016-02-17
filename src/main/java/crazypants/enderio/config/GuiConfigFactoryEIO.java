package crazypants.enderio.config;

import java.util.ArrayList;
import java.util.List;

import static crazypants.enderio.config.Config.config;

import crazypants.enderio.EnderIO;
import crazypants.enderio.config.Config.Section;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;

public class GuiConfigFactoryEIO extends GuiConfig {

  public GuiConfigFactoryEIO(GuiScreen parentScreen) {
    super(parentScreen, getConfigElements(parentScreen), EnderIO.MODID, false, false, EnderIO.lang.localize("config.title"));
  }

  private static List<IConfigElement> getConfigElements(GuiScreen parent) {
    List<IConfigElement> list = new ArrayList<IConfigElement>();
    String prefix = EnderIO.lang.addPrefix("config.");

    for (Section section : Config.sections) {
      list.add(new ConfigElement(config.getCategory(section.lc()).setLanguageKey(prefix + section.lang)));
    }

    return list;
  }
}
