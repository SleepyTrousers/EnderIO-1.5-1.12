package crazypants.enderio.conduit.gui;

import java.awt.Color;

import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.gui.IconEIO;
import crazypants.render.ColorUtil;

public class BaseSettingsPanel implements ISettingsPanel {

  private final IconEIO icon;
  private final GuiExternalConnection gui;
  private final IConduit con;
  private final String typeName;

  protected BaseSettingsPanel(IconEIO icon, String typeName, GuiExternalConnection gui, IConduit con) {
    this.icon = icon;
    this.typeName = typeName;
    this.gui = gui;
    this.con = con;
  }

  @Override
  public void activate() {
  }

  @Override
  public void deactivate() {
  }

  @Override
  public IconEIO getIcon() {
    return icon;
  }

  @Override
  public void render(int x, int y, int width, int height) {
    int rgb = ColorUtil.getRGB(Color.darkGray);
    gui.getFontRenderer().drawString(gui.dir + ": " + getTypeName(), x, y, rgb);

    gui.getFontRenderer().drawString("Mode: " + con.getConectionMode(gui.dir), x, y + 5 + gui.getFontRenderer().FONT_HEIGHT, rgb);
  }

  protected String getTypeName() {
    return typeName;
  }

}
