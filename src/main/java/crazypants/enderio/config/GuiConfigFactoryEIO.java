package crazypants.enderio.config;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import cpw.mods.fml.client.config.GuiConfig;
import cpw.mods.fml.client.config.IConfigElement;
import crazypants.enderio.EnderIO;

public class GuiConfigFactoryEIO extends GuiConfig {

  public GuiConfigFactoryEIO(GuiScreen parentScreen)
  {
    super(parentScreen, getConfigElements(parentScreen), EnderIO.MODID, false, false, I18n.format("EIO.config.title"));
  }

  private static List<IConfigElement> getConfigElements(GuiScreen parent)
  {
    List<IConfigElement> list = new ArrayList<IConfigElement>();
    return list;
  }
  
  // Help me bspkrs, you're my only hope.
}
