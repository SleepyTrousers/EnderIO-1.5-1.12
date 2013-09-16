package crazypants.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;

import org.lwjgl.opengl.GL11;

import crazypants.render.RenderUtil;

public class ItemButton extends GuiButton {

  public static final RenderItem ITEM_RENDERER = new RenderItem();

  public static final int DEFAULT_WIDTH = 24;
  public static final int HWIDTH = DEFAULT_WIDTH / 2;
  public static final int DEFAULT_HEIGHT = 24;
  public static final int HHEIGHT = DEFAULT_HEIGHT / 2;

  private ItemStack item;

  private FontRenderer fr;

  protected int hwidth;
  protected int hheight;

  public ItemButton(FontRenderer fr, int id, int x, int y, int itemId) {
    super(id, x, y, DEFAULT_WIDTH, DEFAULT_HEIGHT, "");
    this.fr = fr;
    item = new ItemStack(itemId, 1, 0);
    hwidth = HWIDTH;
    hheight = HHEIGHT;
  }

  public void setSize(int width, int height) {
    this.width = width;
    this.height = height;
    hwidth = width / 2;
    hheight = height / 2;
  }

  /**
   * Draws this button to the screen.
   */
  @SuppressWarnings("synthetic-access")
  @Override
  public void drawButton(Minecraft par1Minecraft, int par2, int par3) {
    if (drawButton) {

      RenderUtil.bindTexture("textures/gui/widgets.png");
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.field_82253_i = par2 >= this.xPosition && par3 >= this.yPosition && par2 < this.xPosition + width
          && par3 < this.yPosition + height;
      int hoverState = this.getHoverState(this.field_82253_i);

      // x, y, u, v, width, height

      // top half
      drawTexturedModalRect(xPosition, yPosition, 0, 46 + hoverState * 20, hwidth, hheight);
      drawTexturedModalRect(xPosition + hwidth, yPosition, 200 - hwidth, 46 + hoverState * 20, hwidth, hheight);

      // bottom half
      drawTexturedModalRect(xPosition, yPosition + hheight, 0, 66 - hheight + (hoverState * 20), hwidth, hheight);
      drawTexturedModalRect(xPosition + hwidth, yPosition + hheight, 200 - hwidth, 66 - hheight + (hoverState * 20), hwidth, hheight);

      mouseDragged(par1Minecraft, par2, par3);

      int l = 14737632;

      if (!this.enabled) {
        l = -6250336;
      } else if (this.field_82253_i) {
        l = 16777120;
      }

      int xLoc = xPosition + hwidth - 8;
      int yLoc = yPosition + hheight - 10;
      ITEM_RENDERER.renderItemIntoGUI(fr, par1Minecraft.renderEngine, item, xLoc, yLoc);
    }
  }

}