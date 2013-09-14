package crazypants.gui;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;

public interface IGuiScreen {

  void addToolTip(GuiToolTip toolTip);
  
  public int getGuiLeft();

  public int getGuiTop();

  public int getXSize();
  
  public void addButton(GuiButton button);
  
}
