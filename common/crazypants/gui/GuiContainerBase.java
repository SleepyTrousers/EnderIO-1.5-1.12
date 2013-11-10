package crazypants.gui;

import java.util.Iterator;
import java.util.List;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.inventory.Container;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import crazypants.gui.ToolTipManager.ToolTipRenderer;

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
    copyOfdrawHoveringText(par1List, par2, par3, font);
    GL11.glPopAttrib();
    GL11.glPopAttrib();
  }

  //This is a copy of the super class method due to 'Method not found' errors
  // reported with some mods installed.
  protected void copyOfdrawHoveringText(List par1List, int par2, int par3, FontRenderer font) {
    if(!par1List.isEmpty())
    {
      GL11.glDisable(GL12.GL_RESCALE_NORMAL);
      RenderHelper.disableStandardItemLighting();
      GL11.glDisable(GL11.GL_LIGHTING);
      GL11.glDisable(GL11.GL_DEPTH_TEST);
      int k = 0;
      Iterator iterator = par1List.iterator();

      while (iterator.hasNext())
      {
        String s = (String) iterator.next();
        int l = font.getStringWidth(s);

        if(l > k)
        {
          k = l;
        }
      }

      int i1 = par2 + 12;
      int j1 = par3 - 12;
      int k1 = 8;

      if(par1List.size() > 1)
      {
        k1 += 2 + (par1List.size() - 1) * 10;
      }

      if(i1 + k > this.width)
      {
        i1 -= 28 + k;
      }

      if(j1 + k1 + 6 > this.height)
      {
        j1 = this.height - k1 - 6;
      }

      this.zLevel = 300.0F;
      itemRenderer.zLevel = 300.0F;
      int l1 = -267386864;
      this.drawGradientRect(i1 - 3, j1 - 4, i1 + k + 3, j1 - 3, l1, l1);
      this.drawGradientRect(i1 - 3, j1 + k1 + 3, i1 + k + 3, j1 + k1 + 4, l1, l1);
      this.drawGradientRect(i1 - 3, j1 - 3, i1 + k + 3, j1 + k1 + 3, l1, l1);
      this.drawGradientRect(i1 - 4, j1 - 3, i1 - 3, j1 + k1 + 3, l1, l1);
      this.drawGradientRect(i1 + k + 3, j1 - 3, i1 + k + 4, j1 + k1 + 3, l1, l1);
      int i2 = 1347420415;
      int j2 = (i2 & 16711422) >> 1 | i2 & -16777216;
      this.drawGradientRect(i1 - 3, j1 - 3 + 1, i1 - 3 + 1, j1 + k1 + 3 - 1, i2, j2);
      this.drawGradientRect(i1 + k + 2, j1 - 3 + 1, i1 + k + 3, j1 + k1 + 3 - 1, i2, j2);
      this.drawGradientRect(i1 - 3, j1 - 3, i1 + k + 3, j1 - 3 + 1, i2, i2);
      this.drawGradientRect(i1 - 3, j1 + k1 + 2, i1 + k + 3, j1 + k1 + 3, j2, j2);

      for (int k2 = 0; k2 < par1List.size(); ++k2)
      {
        String s1 = (String) par1List.get(k2);
        font.drawStringWithShadow(s1, i1, j1, -1);

        if(k2 == 0)
        {
          j1 += 2;
        }

        j1 += 10;
      }

      this.zLevel = 0.0F;
      itemRenderer.zLevel = 0.0F;
      GL11.glEnable(GL11.GL_LIGHTING);
      GL11.glEnable(GL11.GL_DEPTH_TEST);
      RenderHelper.enableStandardItemLighting();
      GL11.glEnable(GL12.GL_RESCALE_NORMAL);
    }
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
