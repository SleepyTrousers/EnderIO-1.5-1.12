package crazypants.gui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.lwjgl.opengl.GL11;

import crazypants.gui.ToolTipManager.ToolTipRenderer;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;

public abstract class GuiContainerBase extends GuiContainer implements ToolTipRenderer, IGuiScreen {

  protected ToolTipManager ttMan = new ToolTipManager();

  protected GuiContainerBase(Container par1Container) {
    super(par1Container);
  }

  @Override
  public void addToolTip(GuiToolTip toolTip) {
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

  @Override
  public int getGuiLeft() {
    return guiLeft;
  }

  @Override
  public int getGuiTop() {
    return guiTop;
  }

  @Override
  public int getXSize() {
    return xSize;
  }

  @Override
  public FontRenderer getFontRenderer() {
    return fontRenderer;
  }
  
  @Override
  public void addButton(GuiButton button) {
    buttonList.add(button);    
  }

}
