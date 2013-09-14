package crazypants.enderio.gui;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.Icon;
import net.minecraft.util.ResourceLocation;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.render.RenderUtil;

public final class IconEIO {

  public static final IconEIO TICK = new IconEIO(0, 100); 
  public static final IconEIO MINUS = new IconEIO(16, 100);
  public static final IconEIO PUBLIC = new IconEIO(32, 100);
  public static final IconEIO PRIVATE = new IconEIO(48, 100);
  public static final IconEIO CROSS = new IconEIO(64, 100);
  public static final IconEIO PLUS = new IconEIO(80, 100);
  
  public static final IconEIO BUTTON = new IconEIO(0, 116); 
  public static final IconEIO BUTTON_HIGHLIGHT = new IconEIO(16, 116);
  public static final IconEIO BUTTON_DISABLED = new IconEIO(32, 116);
  public static final IconEIO BUTTON_DOWN  = new IconEIO(48, 116);
  public static final IconEIO BUTTON_DOWN_HIGHLIGHT = new IconEIO(64, 116);
  
  
  private static final int TEX_SIZE = 256;
  private static final double PIX_SIZE = 1d / TEX_SIZE;

  public final double minU;
  public final double maxU;
  public final double minV;
  public final double maxV;

  public static final ResourceLocation TEXTURE = new ResourceLocation("enderio:textures/gui/widgets.png");

  public IconEIO(int x, int y) {
    this(x,y,16,16);
  }
  
  public IconEIO(int x, int y, int width, int height) {
    this((float) (PIX_SIZE * x), (float) (PIX_SIZE * (x + width )), (float) (PIX_SIZE * y), (float) (PIX_SIZE * (y + height)));
  }
  
  public IconEIO(double minU, double maxU, double minV, double maxV) {
    this.minU = minU;
    this.maxU = maxU;
    this.minV = minV;
    this.maxV = maxV;
//    this.minU = maxU;
//    this.maxU = minU;
//    this.minV = maxV;
//    this.maxV = minV;
  }


  public void renderIcon(double x, double y) {
    renderIcon(x,y,16,16,0,false);
  }
  
  public void renderIcon(double x, double y, double width, double height, double zLevel, boolean doDraw) {

    Tessellator tessellator = Tessellator.instance;
    if (doDraw) {
      RenderUtil.bindTexture(TEXTURE);
      GL11.glColor3f(1, 1, 1);
      tessellator.startDrawingQuads();
    }
    tessellator.addVertexWithUV(x, y + height, zLevel, minU, maxV);
    tessellator.addVertexWithUV(x + width, y + height, zLevel, maxU, maxV);
    tessellator.addVertexWithUV(x + width, y + 0, zLevel, maxU, minV);
    tessellator.addVertexWithUV(x, y + 0, zLevel, minU, minV);    
    if (doDraw) {
      tessellator.draw();
    }
  }

}
