package crazypants.enderio.machines.config.gui;

import java.util.ArrayList;
import java.util.List;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.machines.EnderIOMachines;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;

import static crazypants.enderio.machines.config.Config.config;

public class GuiConfigFactory extends GuiConfig {

  public GuiConfigFactory(GuiScreen parentScreen) {
    super(parentScreen, getConfigElements(parentScreen), EnderIOMachines.MODID, false, false, EnderIO.lang.localize("config.title"));
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
