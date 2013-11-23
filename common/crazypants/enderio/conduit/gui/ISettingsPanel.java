package crazypants.enderio.conduit.gui;

import crazypants.enderio.gui.IconEIO;

public interface ISettingsPanel {

  void activate();

  void deactivate();

  IconEIO getIcon();

  void render(int x, int y, int width, int height);

}
