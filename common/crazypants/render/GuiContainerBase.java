package crazypants.render;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.lwjgl.opengl.GL11;

import crazypants.render.ToolTipManager.ToolTipRenderer;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;

public abstract class GuiContainerBase extends GuiContainer implements ToolTipRenderer {

  protected ToolTipManager ttMan = new ToolTipManager();

  protected GuiContainerBase(Container par1Container) {
    super(par1Container);
  }

  protected void addToolTip(GuiToolTip toolTip) {
    ttMan.addToolTip(toolTip);
  }

  @Override
  protected final void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
    drawForegroundImpl(mouseX, mouseY);
    ttMan.drawTooltips(this, mouseX, mouseY);
  }

  protected void drawForegroundImpl(int mouseX, int mouseY) {
    super.drawGuiContainerForegroundLayer(mouseX, mouseY);
  }

  @Override
  public void drawHoveringText(List par1List, int par2, int par3, FontRenderer font) {
    GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
    GL11.glPushAttrib(GL11.GL_LIGHTING_BIT);
    super.drawHoveringText(par1List, par2, par3, font);
    GL11.glPopAttrib();
    GL11.glPopAttrib();
  }

  public int getGuiLeft() {
    return guiLeft;
  }

  public int getGuiTop() {
    return guiTop;
  }

  public int getXSize() {
    return xSize;
  }

  public FontRenderer getFontRenderer() {
    return fontRenderer;
  }

}
