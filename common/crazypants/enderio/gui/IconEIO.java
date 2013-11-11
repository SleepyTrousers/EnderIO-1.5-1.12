package crazypants.enderio.gui;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import crazypants.render.RenderUtil;

public final class IconEIO {

  public static final IconEIO TICK = new IconEIO(0, 100);
  public static final IconEIO MINUS = new IconEIO(16, 100);
  public static final IconEIO PUBLIC = new IconEIO(32, 100);
  public static final IconEIO PRIVATE = new IconEIO(48, 100);
  public static final IconEIO CROSS = new IconEIO(64, 100);
  public static final IconEIO PLUS = new IconEIO(80, 100);

  public static final IconEIO BUTTON = new IconEIO(0, 116);
  public static final IconEIO CHECKED_BUTTON = new IconEIO(0, 132);
  public static final IconEIO BUTTON_HIGHLIGHT = new IconEIO(16, 116);
  public static final IconEIO BUTTON_DISABLED = new IconEIO(32, 116);
  public static final IconEIO BUTTON_DOWN = new IconEIO(48, 116);
  public static final IconEIO BUTTON_DOWN_HIGHLIGHT = new IconEIO(64, 116);

  public static final IconEIO WRENCH_OVERLAY_ALL_ON = new IconEIO(0, 148, 32, 32);
  public static final IconEIO WRENCH_OVERLAY_ALL_OFF = new IconEIO(32, 148, 32, 32);
  public static final IconEIO WRENCH_OVERLAY_POWER = new IconEIO(0, 148);
  public static final IconEIO WRENCH_OVERLAY_REDSTONE = new IconEIO(16, 148);
  public static final IconEIO WRENCH_OVERLAY_ITEM = new IconEIO(0, 164);
  public static final IconEIO WRENCH_OVERLAY_FLUID = new IconEIO(16, 164);

  private static final int TEX_SIZE = 256;
  private static final double PIX_SIZE = 1d / TEX_SIZE;

  public final double minU;
  public final double maxU;
  public final double minV;
  public final double maxV;
  public final double width;
  public final double height;

  public static final ResourceLocation TEXTURE = new ResourceLocation("enderio:textures/gui/widgets.png");

  public IconEIO(int x, int y) {
    this(x, y, 16, 16);
  }

  public IconEIO(int x, int y, int width, int height) {
    this(width, height, (float) (PIX_SIZE * x), (float) (PIX_SIZE * (x + width)), (float) (PIX_SIZE * y), (float) (PIX_SIZE * (y + height)));
  }

  public IconEIO(double width, double height, double minU, double maxU, double minV, double maxV) {
    this.width = width;
    this.height = height;
    this.minU = minU;
    this.maxU = maxU;
    this.minV = minV;
    this.maxV = maxV;
  }

  public void renderIcon(double x, double y) {
    renderIcon(x, y, width, height, 0, false);
  }

  public void renderIcon(double x, double y, double width, double height, double zLevel, boolean doDraw) {

    Tessellator tessellator = Tessellator.instance;
    if(doDraw) {
      RenderUtil.bindTexture(TEXTURE);
      tessellator.startDrawingQuads();
    }
    tessellator.addVertexWithUV(x, y + height, zLevel, minU, maxV);
    tessellator.addVertexWithUV(x + width, y + height, zLevel, maxU, maxV);
    tessellator.addVertexWithUV(x + width, y + 0, zLevel, maxU, minV);
    tessellator.addVertexWithUV(x, y + 0, zLevel, minU, minV);
    if(doDraw) {
      tessellator.draw();
    }
  }

}
