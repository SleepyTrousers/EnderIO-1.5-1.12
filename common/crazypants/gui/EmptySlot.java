package crazypants.gui;

import net.minecraft.client.gui.GuiScreen;

import org.lwjgl.opengl.GL11;

import crazypants.render.RenderUtil;

class EmptySlot {

  private static final int WIDTH = ItemButton.DEFAULT_WIDTH;
  private static final int HWIDTH = ItemButton.HWIDTH;
  private static final int HEIGHT = ItemButton.DEFAULT_HEIGHT;
  private static final int HHEIGHT = ItemButton.HHEIGHT;

  private int xPosition;
  private int yPosition;
  private GuiScreen g;

  public EmptySlot(GuiScreen g, int x, int y) {
    xPosition = x;
    yPosition = y;
    this.g = g;

  }

  @SuppressWarnings("synthetic-access")
  public void drawSlot() {
    RenderUtil.bindItemTexture();
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

    int hoverState = 0;
    // top half
    g.drawTexturedModalRect(xPosition, yPosition, 0, 46 + hoverState * 20, HWIDTH, HHEIGHT);
    g.drawTexturedModalRect(xPosition + HWIDTH, yPosition, 200 - HWIDTH, 46 + hoverState * 20, HWIDTH, HHEIGHT);
    // bottom half
    g.drawTexturedModalRect(xPosition, yPosition + HHEIGHT, 0, 64 - HHEIGHT + (hoverState * 20), HWIDTH, HHEIGHT);
    g.drawTexturedModalRect(xPosition + HWIDTH, yPosition + HHEIGHT, 200 - HWIDTH, 64 - HHEIGHT + (hoverState * 20), HWIDTH, HHEIGHT);

  }

}