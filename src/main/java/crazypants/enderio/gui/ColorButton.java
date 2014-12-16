package crazypants.enderio.gui;

import java.awt.Rectangle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemDye;
import net.minecraft.util.MathHelper;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import crazypants.enderio.api.DyeColor;
import crazypants.gui.IGuiScreen;

public class ColorButton extends IconButtonEIO {

  private int colorIndex = 0;

  private String tooltipPrefix = "";
  
  private boolean rightMouseDown = false;

  public ColorButton(IGuiScreen gui, int id, int x, int y) {
    super(gui, id, x, y, null);
  }

  @Override
  public boolean mousePressed(Minecraft par1Minecraft, int par2, int par3) {
    boolean result = super.mousePressed(par1Minecraft, par2, par3);
    if(result) {
      nextColor();
    }
    return result;
  }

  public String getTooltipPrefix() {
    return tooltipPrefix;
  }

  public void setToolTipHeading(String tooltipPrefix) {
    if(tooltipPrefix == null) {
      this.tooltipPrefix = "";
    } else {
      this.tooltipPrefix = tooltipPrefix;
    }
  }

  private void nextColor() {
    colorIndex++;
    if(colorIndex >= ItemDye.field_150923_a.length) {
      colorIndex = 0;
    }
    setColorIndex(colorIndex);
  }
  
  private void prevColor() {
    colorIndex--;
    if(colorIndex < 0) {
      colorIndex = ItemDye.field_150923_a.length - 1;
    }
    setColorIndex(colorIndex);    
  }

  public int getColorIndex() {
    return colorIndex;
  }

  public void setColorIndex(int colorIndex) {
    this.colorIndex = MathHelper.clamp_int(colorIndex, 0, ItemDye.field_150923_a.length - 1);
    String colStr = DyeColor.values()[colorIndex].getLocalisedName();
    if(tooltipPrefix != null && tooltipPrefix.length() > 0) {
      setToolTip(tooltipPrefix, colStr);
    } else {
      setToolTip(colStr);
    }
  }

  @Override
  public void drawButton(Minecraft mc, int mouseX, int mouseY) {
    super.drawButton(mc, mouseX, mouseY);
    if(visible) {
            
      Rectangle r = new Rectangle(xPosition, yPosition, width, height);
      if(r.contains(mouseX, mouseY)) {
        if(rightMouseDown && Mouse.getEventButton() == 1 && !Mouse.getEventButtonState()) {
          prevColor();
          gui.doActionPerformed(this);
        }
        rightMouseDown = Mouse.getEventButton() == 1 && Mouse.getEventButtonState();
      } else {
        rightMouseDown = false;
      }
      
      Tessellator tes = Tessellator.instance;
      tes.startDrawingQuads();

      int x = xPosition + 2;
      int y = yPosition + 2;

      GL11.glDisable(GL11.GL_TEXTURE_2D);

      int col = ItemDye.field_150922_c[colorIndex];
      tes.setColorOpaque_I(col);
      tes.addVertex(x, y + height - 4, zLevel);
      tes.addVertex(x + width - 4, y + height - 4, zLevel);
      tes.addVertex(x + width - 4, y + 0, zLevel);
      tes.addVertex(x, y + 0, zLevel);

      tes.draw();

      GL11.glEnable(GL11.GL_TEXTURE_2D);

    }
  }
}
