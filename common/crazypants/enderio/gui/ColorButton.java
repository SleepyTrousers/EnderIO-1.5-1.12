package crazypants.enderio.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemDye;
import net.minecraft.util.MathHelper;

import org.lwjgl.opengl.GL11;

import crazypants.gui.IGuiScreen;
import crazypants.util.DyeColor;

public class ColorButton extends IconButtonEIO {

  private int colorIndex = 0;

  private String tooltipPrefix = "";

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
    if(colorIndex >= ItemDye.dyeColors.length) {
      colorIndex = 0;
    }
    setColorIndex(colorIndex);
  }

  public int getColorIndex() {
    return colorIndex;
  }

  public void setColorIndex(int colorIndex) {
    this.colorIndex = MathHelper.clamp_int(colorIndex, 0, ItemDye.dyeColors.length - 1);
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
    if(drawButton) {
      Tessellator tes = Tessellator.instance;
      tes.startDrawingQuads();

      int x = xPosition + 2;
      int y = yPosition + 2;

      GL11.glDisable(GL11.GL_TEXTURE_2D);

      int col = ItemDye.dyeColors[colorIndex];
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
