package crazypants.enderio.gui;

import java.awt.Rectangle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.Icon;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import crazypants.gui.GuiContainerBase;
import crazypants.gui.GuiScreenBase;
import crazypants.gui.GuiToolTip;
import crazypants.gui.IGuiScreen;
import crazypants.render.RenderUtil;

public class IconButtonEIO extends GuiButton {

  public static final int DEFAULT_WIDTH = 16;
  public static final int DEFAULT_HEIGHT = 16;

  protected IconEIO icon;
  protected ResourceLocation texture;
  private int xOrigin;
  private int yOrigin;

  protected GuiScreenBase gui;
  
  protected GuiToolTip toolTip;
  
  public IconButtonEIO(GuiScreenBase gui, int id, int x, int y, IconEIO icon) {
    super(id, x, y, DEFAULT_WIDTH, DEFAULT_HEIGHT, "");
    this.gui = gui;
    this.icon = icon;
    texture = IconEIO.TEXTURE;
    this.xOrigin = x;
    this.yOrigin = y;
  }
  
  public void setToolTip(String... tooltipText) {
    if(toolTip == null) {
      toolTip = new GuiToolTip(new Rectangle(xOrigin,yOrigin,width,height), tooltipText);
      gui.addToolTip(toolTip);
    } else {
      toolTip.setToolTipText(tooltipText);
    }       
  }

  public void onGuiInit() {
    gui.addButton(this);
    xPosition = xOrigin + gui.getGuiLeft();
    yPosition = yOrigin + gui.getGuiTop();
  }    

  public void setSize(int width, int height) {
    this.width = width;
    this.height = height;
    if(toolTip != null) {
      toolTip.setBounds(new Rectangle(xPosition, yPosition, width, height));
    }
  }

  public IconEIO getIcon() {
    return icon;
  }

  public void setIcon(IconEIO icon) {
    this.icon = icon;
  }

  /**
   * Draws this button to the screen.
   */
  @SuppressWarnings("synthetic-access")
  @Override
  public void drawButton(Minecraft mc, int mouseX, int mouseY) {
    if (drawButton) {

      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.field_82253_i = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + width
          && mouseY < this.yPosition + height;
      int hoverState = getHoverState(this.field_82253_i);
      mouseDragged(mc, mouseX, mouseY);

      IconEIO background = getIconForHoverState(hoverState);

      RenderUtil.bindTexture(texture);
      GL11.glColor3f(1, 1, 1);

      Tessellator tes = Tessellator.instance;
      tes.startDrawingQuads();

      int x = xPosition;
      int y = yPosition;

      GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
      GL11.glEnable(GL11.GL_BLEND);
      GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
      
      background.renderIcon(x, y, width, height, 0, false);            
      icon.renderIcon(x, y, width, height, 0, false);

      tes.draw();

      GL11.glPopAttrib();

    }
  }
  
  protected IconEIO getIconForHoverState(int hoverState) {
    if (hoverState == 0) {
      return IconEIO.BUTTON_DISABLED;
    }
    if (hoverState == 2) {
      return IconEIO.BUTTON_HIGHLIGHT;
    }
    return IconEIO.BUTTON;
  }  

}
