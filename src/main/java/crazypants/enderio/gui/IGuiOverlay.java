package crazypants.enderio.gui;

import java.awt.Rectangle;

import net.minecraft.util.IIcon;
import crazypants.gui.IGuiScreen;

public interface IGuiOverlay {

  void init(IGuiScreen screen);

  IIcon getIcon();

  Rectangle getBounds();

  void draw(int mouseX, int mouseY, float partialTick);

  //  //consume event?
  //  boolean mouseClicked(int par1, int par2, int par3);
  //
  //  boolean mouseClickMove(int par1, int par2, int par3, long p_146273_4_);
  //
  //  boolean mouseMovedOrUp(int par1, int par2, int par3);

  void setVisible(boolean visible);

  boolean isVisible();

  boolean handleMouseInput(int x, int y, int b);

  boolean isMouseInBounds(int mouseX, int mouseY);

}
