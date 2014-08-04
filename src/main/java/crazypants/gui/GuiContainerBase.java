package crazypants.gui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.inventory.Container;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import crazypants.enderio.gui.IGuiOverlay;
import crazypants.gui.ToolTipManager.ToolTipRenderer;

public abstract class GuiContainerBase extends GuiContainer implements ToolTipRenderer, IGuiScreen {

  protected ToolTipManager ttMan = new ToolTipManager();
  protected List<IGuiOverlay> overlays = new ArrayList<IGuiOverlay>();

  protected GuiContainerBase(Container par1Container) {
    super(par1Container);
  }

  @Override
  public void initGui() {
    super.initGui();
    for(IGuiOverlay overlay : overlays) {
      overlay.init(this);
    }
  }

  @Override
  protected void keyTyped(char par1, int par2) {
    if (par2 == 1 || par2 == this.mc.gameSettings.keyBindInventory.getKeyCode()) {
      //this.mc.thePlayer.closeScreen();
      for(IGuiOverlay overlay : overlays) {
        if(overlay.isVisible()) {
          overlay.setVisible(false);
          return;
        }
      }
    }
    super.keyTyped(par1, par2);
  }


  @Override
  public void addToolTip(GuiToolTip toolTip) {
    ttMan.addToolTip(toolTip);
  }

  @Override
  public void handleMouseInput() {
    int x = Mouse.getEventX() * this.width / this.mc.displayWidth;
    int y = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
    int b = Mouse.getEventButton();
    for(IGuiOverlay overlay : overlays) {
      if(overlay != null && overlay.isVisible() && overlay.handleMouseInput(x, y, b)) {
        return;
      }
    }
    super.handleMouseInput();
  }

  @Override
  protected boolean func_146978_c(int p_146978_1_, int p_146978_2_, int p_146978_3_, int p_146978_4_, int p_146978_5_, int p_146978_6_)  {
    int x = Mouse.getEventX() * this.width / this.mc.displayWidth;
    int y = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
    int b = Mouse.getEventButton();
    for(IGuiOverlay overlay : overlays) {
      if(overlay != null && overlay.isVisible() && overlay.isMouseInBounds(x, y)) {
        return false;
      }
    }
    return super.func_146978_c(p_146978_1_, p_146978_2_, p_146978_3_, p_146978_4_, p_146978_5_, p_146978_6_);
  }

  public void addOverlay(IGuiOverlay overlay) {
    overlays.add(overlay);
  }

  public void removeOverlay(IGuiOverlay overlay) {
    overlays.remove(overlay);
  }

  @Override
  protected final void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
    drawForegroundImpl(mouseX, mouseY);
    ttMan.drawTooltips(this, mouseX, mouseY);
  }

  @Override
  public void drawScreen(int par1, int par2, float par3) {

    int mx = par1;
    int my = par2;
    for(IGuiOverlay overlay : overlays) {
      if(overlay != null && overlay.isVisible() && isMouseInOverlay(par1, par2, overlay)) {
        mx = -5000;
        my = -5000;
      }
    }

    super.drawScreen(mx, my, par3);

    GL11.glPushMatrix();
    GL11.glTranslatef(guiLeft, guiTop, 0.0F);
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    GL11.glDisable(GL11.GL_DEPTH_TEST);
    for(IGuiOverlay overlay : overlays) {
      if(overlay != null && overlay.isVisible()) {
        overlay.draw(par1, par2, par3);
      }
    }
    GL11.glEnable(GL11.GL_DEPTH_TEST);
    GL11.glPopMatrix();
  }

  private boolean isMouseInOverlay(int mouseX, int mouseY, IGuiOverlay overlay) {
    int x = mouseX - getGuiLeft();
    int y = mouseY - getGuiTop();
    if(overlay.getBounds().contains(x,y)) {
      return true;
    }
    return false;
  }


  @Override
  public void removeToolTip(GuiToolTip toolTip) {
    ttMan.removeToolTip(toolTip);
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
      //itemRenderer.zLevel = 300.0F;
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
      //itemRenderer.zLevel = 0.0F;
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
  public int getYSize() {
    return ySize;
  }

  @Override
  public FontRenderer getFontRenderer() {
    return Minecraft.getMinecraft().fontRenderer;
  }

  @Override
  public void addButton(GuiButton button) {
    if(!buttonList.contains(button)) {
      buttonList.add(button);
    }
  }

  @Override
  public void removeButton(GuiButton button) {
    buttonList.remove(button);
  }

  @Override
  public int getOverlayOffsetX() {  
    return 0;
  }
  
  

}
