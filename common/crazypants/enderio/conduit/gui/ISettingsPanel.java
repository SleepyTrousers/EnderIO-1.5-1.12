package crazypants.enderio.conduit.gui;

import net.minecraft.client.gui.GuiButton;
import crazypants.enderio.gui.IconEIO;

public interface ISettingsPanel {

  void onGuiInit(int x, int y, int width, int height);

  void deactivate();

  IconEIO getIcon();

  void render(float par1, int par2, int par3);

  void actionPerformed(GuiButton guiButton);

}
