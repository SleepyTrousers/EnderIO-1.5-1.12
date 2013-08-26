package crazypants.render;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import crazypants.render.TooltipManager.TooltipRender;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;

public abstract class GuiContainerBase extends GuiContainer implements TooltipRender {

  protected TooltipManager ttMan = new TooltipManager();

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
    super.drawHoveringText(par1List, par2, par3, font);
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
