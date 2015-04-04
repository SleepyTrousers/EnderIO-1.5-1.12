package crazypants.enderio.config;

import static crazypants.enderio.config.Config.config;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.ConfigElement;
import cpw.mods.fml.client.config.GuiConfig;
import cpw.mods.fml.client.config.IConfigElement;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.EnderIO;
import crazypants.enderio.config.Config.Section;
import crazypants.util.Lang;

@SuppressWarnings({ "rawtypes" })
@SideOnly(Side.CLIENT)
public class GuiConfigFactoryEIO extends GuiConfig {

  public GuiConfigFactoryEIO(GuiScreen parentScreen)
  {
    super(parentScreen, getConfigElements(parentScreen), EnderIO.MODID, false, false, Lang.localize("config.title", true));
  }

  private static List<IConfigElement> getConfigElements(GuiScreen parent)
  {
    List<IConfigElement> list = new ArrayList<IConfigElement>();
    String prefix = Lang.prefix + "config.";

    for (Section section : Config.sections) {
      list.add(new ConfigElement<ConfigCategory>(config.getCategory(section.lc()).setLanguageKey(prefix + section.lang)));
    }

    return list;
  }
}
